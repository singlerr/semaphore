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
import io.github.singlerr.semaphore.policy.CallTimeoutHandler;
import io.github.singlerr.semaphore.policy.PolicyConstants;
import io.github.singlerr.semaphore.policy.dfa.NFA;
import io.github.singlerr.semaphore.policy.dfa.PlayerInput;
import java.util.Optional;
import java.util.UUID;

public final class CallStateMachine implements CallRequestManager {

    private final NFA dfa;
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

        if (!newCallerState.isPresent()) {
            resetState(callee);
            resetState(caller);

            errorPresenter.present(new Error(calleeId, callerId, "error.call.closed.from.caller"));
            CallTimeoutHandler.getInstance().cancelTimeout(new CallTimeoutHandler.Key(callerId, calleeId));
            return;
        }

        if (!newCalleeState.isPresent()) {
            resetState(callee);
            resetState(caller);

            errorPresenter.present(new Error(calleeId, callerId, "error.target.already.in.call"));
            return;
        }

        CallTimeoutHandler.getInstance().startTimeout(new CallTimeoutHandler.Key(callerId, calleeId), () -> {
            callTimeout(callerId, calleeId);
        });

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

    private void callTimeout(UUID callerId, UUID calleeId) {
        Entity caller = database.getById(callerId);
        Entity callee = database.getById(calleeId);

        if (caller != null) {
            database.update(
                    callerId,
                    new Entity(callerId, new Entity.State(0, caller.state().missCallCount())));
            errorPresenter.present(new Error(calleeId, callerId, "error.call.timeout"));
        }

        if (callee != null) {
            callee.state()
                    .missCallCount()
                    .put(callerId, callee.state().missCallCount().getOrDefault(callerId, 0) + 1);
            database.update(
                    callerId,
                    new Entity(callerId, new Entity.State(0, callee.state().missCallCount())));
        }
    }

    private void resetState(Entity entity) {
        database.update(
                entity.id(),
                new Entity(entity.id(), new Entity.State(0, entity.state().missCallCount())));
    }
}
