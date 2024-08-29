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
import io.github.singlerr.semaphore.policy.PolicyConstants;
import io.github.singlerr.semaphore.policy.dfa.PlayerInput;
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
        this.dfa = PolicyConstants.STATE_DFA.clone();
        this.errorPresenter = errorPresenter;
        this.requestPresenter = requestPresenter;
    }

    @Override
    public void request(CallRequest request) {
        UUID callerId = request.callerId();
        UUID calleeId = request.calleeId();

        Entity caller = database.getById(callerId);
        Entity callee = database.getById(calleeId);

        if (caller == null || callee == null) {
            if (caller != null) {
                database.update(
                        callerId,
                        new Entity(callerId, new Entity.State(0, caller.state().missCallCount())));
            }
            if (callee != null) {
                database.update(
                        calleeId,
                        new Entity(calleeId, new Entity.State(0, callee.state().missCallCount())));
            }
            return;
        }

        Optional<Integer> newCallerState = dfa.consume(caller.state().stateId(), PlayerInput.REQUEST_CALL);
        Optional<Integer> newCalleeState = dfa.consume(callee.state().stateId(), PlayerInput.RECEIVE_CALL);

        if (!newCallerState.isPresent() || !newCalleeState.isPresent()) {
            resetState(callerId);
            resetState(calleeId);
            errorPresenter.present(new Error(calleeId, callerId, "상대방이 통화 가능한 상태가 아닙니다."));
            return;
        }

        database.update(
                callerId,
                new Entity(
                        callerId,
                        new Entity.State(newCallerState.get(), caller.state().missCallCount())));
        database.update(
                calleeId,
                new Entity(
                        calleeId,
                        new Entity.State(newCalleeState.get(), callee.state().missCallCount())));

        requestPresenter.present(new InverseCallRequest(callerId, calleeId));
    }

    private void resetState(UUID id) {
        database.update(id, new Entity(id, new Entity.State(0, 0)));
    }
}
