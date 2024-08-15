/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.caller.interactors;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.caller.manager.CallRequestManager;
import io.github.singlerr.semaphore.interactors.caller.manager.data.CallRequest;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.policy.dfa.PlayerInput;
import io.github.singlerr.semaphore.policy.dfa.PlayerState;
import io.github.singlerr.semaphore.policy.dfa.PlayerStateDFA;
import java.util.Optional;
import java.util.UUID;

public final class CallStateMachine implements CallRequestManager {

    private final PlayerStateDFA dfa;
    private final DatabaseGateway database;
    private final ErrorPresenter errorPresenter;
    private final CallRequestPresenter requestPresenter;

    public CallStateMachine(
            DatabaseGateway database, ErrorPresenter errorPresenter, CallRequestPresenter requestPresenter) {
        this.database = database;
        this.dfa = buildDFA();
        this.errorPresenter = errorPresenter;
        this.requestPresenter = requestPresenter;
    }

    private PlayerStateDFA buildDFA() {
        return new PlayerStateDFA.Builder()
                .encode(0, PlayerState.DEFAULT)
                .encode(1, PlayerState.IN_CALL)
                .encode(2, PlayerState.REQUESTING_CALL)
                .encode(3, PlayerState.RECEIVING_CALL)
                .transit(0, PlayerInput.REQUEST_CALL, 1)
                .transit(0, PlayerInput.RECEIVE_CALL, 2)
                .transit(1, PlayerInput.CLOSE_CALL, 0)
                .transit(1, PlayerInput.ACCEPT_CALL, 3)
                .transit(2, PlayerInput.REJECT_CALL, 0)
                .transit(2, PlayerInput.ACCEPT_CALL, 3)
                .transit(3, PlayerInput.CLOSE_CALL, 0)
                .build();
    }

    @Override
    public void request(CallRequest request) {
        UUID callerId = request.callerId();
        UUID calleeId = request.calleeId();

        Entity caller = database.getById(callerId);
        Entity callee = database.getById(calleeId);

        if (caller == null || callee == null) {
            if (caller != null) {
                database.update(callerId, new Entity(callerId, 0));
            }
            if (callee != null) {
                database.update(calleeId, new Entity(calleeId, 0));
            }
            return;
        }

        Optional<Integer> newCallerState = dfa.consume(caller.stateId(), PlayerInput.REQUEST_CALL);
        Optional<Integer> newCalleeState = dfa.consume(callee.stateId(), PlayerInput.RECEIVE_CALL);

        if (!newCallerState.isPresent() || !newCalleeState.isPresent()) {
            resetState(callerId);
            resetState(calleeId);
            errorPresenter.present(new Error(calleeId, callerId, "상대방이 통화 가능한 상태가 아닙니다."));
            return;
        }

        database.update(callerId, new Entity(callerId, newCallerState.get()));
        database.update(calleeId, new Entity(calleeId, newCalleeState.get()));

        requestPresenter.present(new InverseCallRequest(callerId, calleeId));
    }

    private void resetState(UUID id) {
        database.update(id, new Entity(id, 0));
    }
}
