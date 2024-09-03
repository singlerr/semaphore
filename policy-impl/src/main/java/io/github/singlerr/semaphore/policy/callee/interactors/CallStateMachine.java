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

        Optional<Integer> newCallerState;
        Optional<Integer> newCalleeState;
        if (type == ResponseType.ACCEPT) {
            newCallerState = dfa.consume(caller.state().stateId(), PlayerInput.ACCEPT_CALL);
            newCalleeState = dfa.consume(callee.state().stateId(), PlayerInput.ACCEPT_CALL);

            if (!newCallerState.isPresent() || !newCalleeState.isPresent()) {
                resetState(callee);
                resetState(callee);
                errorPresenter.error(new Error(calleeId, callerId, "error.player.not.found"));
                return;
            }

            CallConnection con = callConnectionHandler.open(callerId, calleeId);
            if (con == null || con.state() != CallState.ALIVE) {
                resetState(callee);
                resetState(callee);
                errorPresenter.error(new Error(calleeId, callerId, "error.connection.unavailable"));
                return;
            }

        } else {
            newCallerState = dfa.consume(caller.state().stateId(), PlayerInput.REJECT_CALL);
            newCalleeState = dfa.consume(callee.state().stateId(), PlayerInput.REJECT_CALL);

            if (!newCallerState.isPresent() || !newCalleeState.isPresent()) {
                resetState(callee);
                resetState(callee);
                errorPresenter.error(new Error(calleeId, callerId, "error.player.not.found"));
                return;
            }
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
    }

    private void resetState(Entity entity) {
        database.update(
                entity.id(),
                new Entity(entity.id(), new Entity.State(0, entity.state().missCallCount())));
    }
}
