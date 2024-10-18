/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callee.interactors;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.admin.manager.CallStateManager;
import io.github.singlerr.semaphore.interactors.admin.manager.data.Call;
import io.github.singlerr.semaphore.interactors.admin.manager.data.ConnectionState;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.interactors.callee.manager.CallResponseManager;
import io.github.singlerr.semaphore.interactors.callee.manager.data.ResponseType;
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.ErrorHandler;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.policy.CallTimeoutHandler;
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

    private final EntityPresenter entityPresenter;

    public CallStateMachine(
            DatabaseGateway database,
            CallStateManager callStateManager,
            CallResponsePresenter responsePresenter,
            EntityPresenter entityPresenter) {
        this.database = database;
        this.errorPresenter = responsePresenter;
        this.callStateManager = callStateManager;
        this.dfa = PolicyConstants.STATE_NFA.clone();
        this.responsePresenter = responsePresenter;
        this.entityPresenter = entityPresenter;
    }

    @Override
    public void reply(UUID callerId, UUID calleeId, ResponseType type) {
        Entity caller = database.getById(callerId);
        Entity callee = database.getById(calleeId);

        CallTimeoutHandler.getInstance().cancelTimeout(new CallTimeoutHandler.Key(callerId, calleeId));

        if (caller == null || callee == null) {
            if (caller != null) {
                Entity entity = new Entity(
                        callerId,
                        new Entity.State(
                                0,
                                caller.getState().getMissCallCount(),
                                caller.getState().getEntityType()));
                database.update(callerId, entity);
                errorPresenter.error(new Error(calleeId, callerId, "error.player.not.found"));
                entityPresenter.present(new PresentableEntity(
                        callerId,
                        new PresentableEntity.State(
                                entity.getState().getStateId(),
                                entity.getState().getMissCallCount(),
                                entity.getState().getEntityType())));
            }
            if (callee != null) {
                Entity entity = new Entity(
                        calleeId,
                        new Entity.State(
                                0,
                                callee.getState().getMissCallCount(),
                                callee.getState().getEntityType()));
                database.update(calleeId, entity);
                errorPresenter.error(new Error(calleeId, callerId, "error.player.not.found"));
                entityPresenter.present(new PresentableEntity(
                        calleeId,
                        new PresentableEntity.State(
                                entity.getState().getStateId(),
                                entity.getState().getMissCallCount(),
                                entity.getState().getEntityType())));
            }
            return;
        }

        Optional<Integer> newCallerState;
        Optional<Integer> newCalleeState;
        if (type == ResponseType.ACCEPT) {
            newCallerState = dfa.consume(caller.getState().getStateId(), PlayerInput.ACCEPT_CALL);
            newCalleeState = dfa.consume(callee.getState().getStateId(), PlayerInput.ACCEPT_CALL);

            if (!newCallerState.isPresent() || !newCalleeState.isPresent()) {
                resetState(callee);
                resetState(callee);
                errorPresenter.error(new Error(calleeId, callerId, "error.state"));
                return;
            }

            Call con = callStateManager.openCall(callerId, calleeId);
            if (con == null || con.getState() != ConnectionState.ALIVE) {
                resetState(callee);
                resetState(callee);
                errorPresenter.error(new Error(calleeId, callerId, "error.connection.unavailable"));
                return;
            }

            responsePresenter.present(new CallResponse(callerId, calleeId, CallResponse.ResponseType.ACCEPT));
        } else {
            newCallerState = dfa.consume(caller.getState().getStateId(), PlayerInput.CLOSE_CALL);
            newCalleeState = dfa.consume(callee.getState().getStateId(), PlayerInput.REJECT_CALL);

            if (!newCallerState.isPresent() || !newCalleeState.isPresent()) {
                resetState(callee);
                resetState(callee);
                errorPresenter.error(new Error(calleeId, callerId, "error.player.not.found"));
                return;
            }

            responsePresenter.present(new CallResponse(callerId, calleeId, CallResponse.ResponseType.REJECT));
        }

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
}
