/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callee.interactors;

import io.github.singlerr.semaphore.interactors.access.call.CallConnection;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.call.CallState;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.callee.manager.base.BaseCallResponseManager;
import io.github.singlerr.semaphore.interactors.callee.manager.data.ResponseType;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.policy.PolicyConstants;
import io.github.singlerr.semaphore.policy.dfa.PlayerInput;
import io.github.singlerr.semaphore.policy.dfa.PlayerStateDFA;
import java.util.Optional;
import java.util.UUID;

public final class CallStateMachine extends BaseCallResponseManager {

    private final PlayerStateDFA dfa;

    public CallStateMachine(
            DatabaseGateway database, CalleeInteractor interactor, CallConnectionHandler callConnectionHandler) {
        super(database, interactor, callConnectionHandler);
        this.dfa = PolicyConstants.STATE_DFA.clone();
    }

    @Override
    public void reply(UUID callerId, UUID calleeId, ResponseType type) {
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

        Optional<Integer> newCallerState;
        Optional<Integer> newCalleeState;
        if (type == ResponseType.ACCEPT) {
            newCallerState = dfa.consume(caller.stateId(), PlayerInput.ACCEPT_CALL);
            newCalleeState = dfa.consume(callee.stateId(), PlayerInput.ACCEPT_CALL);

            if (!newCallerState.isPresent() || !newCalleeState.isPresent()) {
                resetState(callerId);
                resetState(calleeId);
                errorPresenter.error(new Error(calleeId, callerId, "상대방이 통화 가능한 상태가 아닙니다."));
                return;
            }

            CallConnection con = callConnectionHandler.open(callerId, calleeId);
            if (con == null || con.state() != CallState.ALIVE) {
                resetState(calleeId);
                resetState(callerId);
                errorPresenter.error(new Error(calleeId, callerId, "통화 가능한 상태가 아닙니다."));
                return;
            }

        } else {
            newCallerState = dfa.consume(caller.stateId(), PlayerInput.REJECT_CALL);
            newCalleeState = dfa.consume(callee.stateId(), PlayerInput.REJECT_CALL);

            if (!newCallerState.isPresent() || !newCalleeState.isPresent()) {
                resetState(callerId);
                resetState(calleeId);
                errorPresenter.error(new Error(calleeId, callerId, "상대방이 통화 가능한 상태가 아닙니다."));
                return;
            }
        }

        database.update(callerId, new Entity(callerId, newCallerState.get()));
        database.update(calleeId, new Entity(calleeId, newCalleeState.get()));
    }

    private void resetState(UUID id) {
        database.update(id, new Entity(id, 0));
    }
}
