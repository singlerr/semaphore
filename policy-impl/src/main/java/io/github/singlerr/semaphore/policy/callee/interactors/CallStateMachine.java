/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callee.interactors;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.admin.manager.CallStateManager;
import io.github.singlerr.semaphore.interactors.admin.manager.data.Call;
import io.github.singlerr.semaphore.interactors.admin.manager.data.ConnectionState;
import io.github.singlerr.semaphore.interactors.callee.manager.CallResponseManager;
import io.github.singlerr.semaphore.interactors.callee.manager.data.ResponseType;
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.ErrorHandler;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.policy.PolicyConstants;
import io.github.singlerr.semaphore.policy.dfa.NFA;
import io.github.singlerr.semaphore.policy.dfa.PlayerInput;
import java.util.Optional;
import java.util.UUID;

public final class CallStateMachine implements CallResponseManager {

    private final DatabaseGateway database;
    private final ErrorHandler errorPresenter;
    private final CallStateManager callStateManager;

    private final NFA dfa;
    private final CallResponsePresenter responsePresenter;

    public CallStateMachine(
            DatabaseGateway database, CallStateManager callStateManager, CallResponsePresenter responsePresenter) {
        this.database = database;
        this.errorPresenter = responsePresenter;
        this.callStateManager = callStateManager;
        this.dfa = PolicyConstants.STATE_DFA.clone();
        this.responsePresenter = responsePresenter;
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
                errorPresenter.error(new Error(calleeId, callerId, "error.player.not.found"));
            }
            if (callee != null) {
                database.update(
                        calleeId,
                        new Entity(calleeId, new Entity.State(0, callee.state().missCallCount())));
                errorPresenter.error(new Error(calleeId, callerId, "error.player.not.found"));
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
                errorPresenter.error(new Error(calleeId, callerId, "error.state"));
                return;
            }

            Call con = callStateManager.openCall(callerId, calleeId);
            if (con == null || con.state() != ConnectionState.ALIVE) {
                resetState(callee);
                resetState(callee);
                errorPresenter.error(new Error(calleeId, callerId, "error.connection.unavailable"));
                return;
            }

            responsePresenter.present(new CallResponse(callerId, calleeId, CallResponse.ResponseType.ACCEPT));
        } else {
            newCallerState = dfa.consume(caller.state().stateId(), PlayerInput.CLOSE_CALL);
            newCalleeState = dfa.consume(callee.state().stateId(), PlayerInput.REJECT_CALL);

            if (!newCallerState.isPresent() || !newCalleeState.isPresent()) {
                resetState(callee);
                resetState(callee);
                errorPresenter.error(new Error(calleeId, callerId, "error.player.not.found"));
                return;
            }

            responsePresenter.present(new CallResponse(callerId, calleeId, CallResponse.ResponseType.REJECT));
        }

        caller = new Entity(
                callerId, new Entity.State(newCallerState.get(), caller.state().missCallCount()));
        callee = new Entity(
                calleeId, new Entity.State(newCalleeState.get(), callee.state().missCallCount()));
        database.update(callerId, caller);
        database.update(calleeId, callee);
    }

    private void resetState(Entity entity) {
        database.update(
                entity.id(),
                new Entity(entity.id(), new Entity.State(0, entity.state().missCallCount())));
    }
}
