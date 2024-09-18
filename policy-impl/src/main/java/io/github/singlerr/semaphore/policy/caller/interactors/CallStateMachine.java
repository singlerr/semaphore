/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.caller.interactors;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
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
import io.github.singlerr.semaphore.policy.utils.LazyUtils;
import java.util.Optional;
import java.util.UUID;

public final class CallStateMachine implements CallRequestManager {

    private final NFA dfa;
    private final DatabaseGateway database;
    private final ErrorPresenter errorPresenter;
    private final CallRequestPresenter requestPresenter;
    private final EntityPresenter entityPresenter;

    public CallStateMachine(
            DatabaseGateway database,
            ErrorPresenter errorPresenter,
            CallRequestPresenter requestPresenter,
            EntityPresenter entityPresenter) {
        this.database = database;
        this.dfa = PolicyConstants.STATE_NFA.clone();
        this.errorPresenter = errorPresenter;
        this.requestPresenter = requestPresenter;
        this.entityPresenter = entityPresenter;
    }

    @Override
    public void request(CallRequest request) {
        UUID callerId = request.getCallerId();
        UUID calleeId = request.getCalleeId();

        Entity caller = database.getById(callerId);
        Entity callee = database.getById(calleeId);

        if (caller == null || callee == null) {

            if (caller != null) {
                Entity entity = new Entity(
                        callerId,
                        new Entity.State(
                                0,
                                caller.getState().getMissCallCount(),
                                caller.getState().getEntityType()));
                database.update(callerId, entity);
                entityPresenter.present(new PresentableEntity(
                        entity.getId(),
                        new PresentableEntity.State(
                                entity.getState().getStateId(),
                                entity.getState().getMissCallCount(),
                                entity.getState().getEntityType())));
                return;
            }
            if (callee != null) {
                Entity entity = new Entity(
                        calleeId,
                        new Entity.State(
                                0,
                                callee.getState().getMissCallCount(),
                                callee.getState().getEntityType()));
                database.update(calleeId, entity);
                entityPresenter.present(new PresentableEntity(
                        entity.getId(),
                        new PresentableEntity.State(
                                entity.getState().getStateId(),
                                entity.getState().getMissCallCount(),
                                entity.getState().getEntityType())));
                return;
            }
        }

        Optional<Integer> newCallerState = dfa.consume(caller.getState().getStateId(), PlayerInput.REQUEST_CALL);
        Optional<Integer> newCalleeState = dfa.consume(callee.getState().getStateId(), PlayerInput.RECEIVE_CALL);

        if (!newCallerState.isPresent()
                && dfa.consume(caller.getState().getStateId(), PlayerInput.CANCEL_CALL)
                        .isPresent()) {
            resetState(caller);

            callee = new Entity(
                    calleeId,
                    new Entity.State(
                            0,
                            LazyUtils.compute(
                                    callee.getState().getMissCallCount(),
                                    m -> m.put(callerId, m.getOrDefault(callerId, 0) + 1)),
                            callee.getState().getEntityType()));
            updateState(callee);
            errorPresenter.present(new Error(calleeId, callerId, "error.call.closed.from.caller"));
            CallTimeoutHandler.getInstance().cancelTimeout(new CallTimeoutHandler.Key(callerId, calleeId));
            return;
        }

        if (!newCalleeState.isPresent()) {
            resetState(caller);
            callee = new Entity(
                    calleeId,
                    new Entity.State(
                            callee.getState().getStateId(),
                            LazyUtils.compute(
                                    callee.getState().getMissCallCount(),
                                    m -> m.put(callerId, m.getOrDefault(callerId, 0) + 1)),
                            callee.getState().getEntityType()));
            updateState(callee);
            errorPresenter.present(new Error(callerId, callerId, "error.target.already.in.call"));
            return;
        }

        CallTimeoutHandler.getInstance().startTimeout(new CallTimeoutHandler.Key(callerId, calleeId), () -> {
            callTimeout(callerId, calleeId);
        });

        caller = new Entity(
                callerId,
                new Entity.State(
                        newCallerState.get(),
                        caller.getState().getMissCallCount(),
                        caller.getState().getEntityType()));
        callee = new Entity(
                calleeId,
                new Entity.State(
                        newCalleeState.get(),
                        callee.getState().getMissCallCount(),
                        callee.getState().getEntityType()));
        database.update(callerId, caller);
        database.update(calleeId, callee);

        requestPresenter.present(new InverseCallRequest(callerId, calleeId));
        entityPresenter.present(new PresentableEntity(
                caller.getId(),
                new PresentableEntity.State(
                        caller.getState().getStateId(),
                        caller.getState().getMissCallCount(),
                        caller.getState().getEntityType())));
        entityPresenter.present(new PresentableEntity(
                callee.getId(),
                new PresentableEntity.State(
                        callee.getState().getStateId(),
                        callee.getState().getMissCallCount(),
                        callee.getState().getEntityType())));
    }

    private void callTimeout(UUID callerId, UUID calleeId) {
        Entity caller = database.getById(callerId);
        Entity callee = database.getById(calleeId);

        if (caller != null) {
            database.update(
                    callerId,
                    new Entity(
                            callerId,
                            new Entity.State(
                                    0,
                                    caller.getState().getMissCallCount(),
                                    caller.getState().getEntityType())));
            errorPresenter.present(new Error(calleeId, callerId, "error.call.timeout"));
        }

        if (callee != null) {
            callee.getState()
                    .getMissCallCount()
                    .put(callerId, callee.getState().getMissCallCount().getOrDefault(callerId, 0) + 1);
            database.update(
                    callerId,
                    new Entity(
                            callerId,
                            new Entity.State(
                                    0,
                                    callee.getState().getMissCallCount(),
                                    callee.getState().getEntityType())));
            errorPresenter.present(new Error(calleeId, callerId, "error.call.timeout"));
        }
    }

    private void resetState(Entity entity) {
        Entity newEntity = new Entity(
                entity.getId(),
                new Entity.State(
                        0,
                        entity.getState().getMissCallCount(),
                        entity.getState().getEntityType()));
        database.update(entity.getId(), newEntity);
        entityPresenter.present(new PresentableEntity(
                newEntity.getId(),
                new PresentableEntity.State(
                        newEntity.getState().getStateId(),
                        newEntity.getState().getMissCallCount(),
                        newEntity.getState().getEntityType())));
    }

    private void updateState(Entity entity) {
        database.update(entity.getId(), entity);
        entityPresenter.present(new PresentableEntity(
                entity.getId(),
                new PresentableEntity.State(
                        entity.getState().getStateId(),
                        entity.getState().getMissCallCount(),
                        entity.getState().getEntityType())));
    }
}
