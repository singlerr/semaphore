/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy;

import static org.junit.jupiter.api.Assertions.*;

import io.github.singlerr.semaphore.interactors.access.call.CallConnection;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.call.CallState;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.access.database.EntityType;
import io.github.singlerr.semaphore.interactors.admin.manager.CallStateManager;
import io.github.singlerr.semaphore.interactors.admin.manager.data.Call;
import io.github.singlerr.semaphore.interactors.admin.manager.data.ConnectionState;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.callee.manager.data.ResponseType;
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.ErrorHandler;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;
import io.github.singlerr.semaphore.interactors.caller.manager.data.CallRequest;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.policy.callee.SimpleCalleeInteractor;
import io.github.singlerr.semaphore.policy.caller.SimpleCallerInteractor;
import io.github.singlerr.semaphore.policy.database.PlayerDatabase;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CallActionTest {

    @Test
    void testRequestCallAndAccept() {
        DatabaseGateway stubDatabase = new PlayerDatabase();
        CallConnectionHandler stubCallConnectionHandler = new StubCallConnectionHandler();
        EntityPresenter stubEntityPresenter = new StubEntityPresenter();
        CalleeInteractor stubCalleeInteractor = new SimpleCalleeInteractor(
                stubDatabase,
                new StubCallStateManager(),
                new StubErrorHandler(),
                new StubResponsePresenter(),
                stubEntityPresenter);
        CallerInteractor stubCallerInteractor = new SimpleCallerInteractor(
                stubDatabase, new StubErrorPresenter(), new StubRequestPresenter(), stubEntityPresenter);

        Entity stubCaller = new Entity(UUID.randomUUID(), new Entity.State(0, new HashMap<>(), EntityType.PLAYER));
        Entity stubCallee = new Entity(UUID.randomUUID(), new Entity.State(0, new HashMap<>(), EntityType.PLAYER));

        stubDatabase.create(stubCaller.getId(), stubCaller);
        stubDatabase.create(stubCallee.getId(), stubCallee);

        // 1. Request call
        stubCallerInteractor.getCallRequestManager().request(new CallRequest(stubCaller.getId(), stubCallee.getId()));

        assertEquals(1, stubDatabase.getById(stubCaller.getId()).getState().getStateId());
        assertEquals(2, stubDatabase.getById(stubCallee.getId()).getState().getStateId());

        // 2. Accept call
        stubCalleeInteractor.getResponseManager().reply(stubCaller.getId(), stubCallee.getId(), ResponseType.ACCEPT);

        assertEquals(3, stubDatabase.getById(stubCaller.getId()).getState().getStateId());
        assertEquals(3, stubDatabase.getById(stubCallee.getId()).getState().getStateId());

        // 3. Reset and reject call
        stubDatabase.update(
                stubCaller.getId(),
                new Entity(stubCaller.getId(), new Entity.State(0, new HashMap<>(), EntityType.PLAYER)));
        stubDatabase.update(
                stubCallee.getId(),
                new Entity(stubCallee.getId(), new Entity.State(0, new HashMap<>(), EntityType.PLAYER)));

        stubCallerInteractor.getCallRequestManager().request(new CallRequest(stubCaller.getId(), stubCallee.getId()));

        stubCalleeInteractor.getResponseManager().reply(stubCaller.getId(), stubCallee.getId(), ResponseType.REJECT);

        assertEquals(0, stubDatabase.getById(stubCaller.getId()).getState().getStateId());
        assertEquals(0, stubDatabase.getById(stubCallee.getId()).getState().getStateId());
    }

    private static class StubCallStateManager implements CallStateManager {

        private UUID callerId;
        private UUID calleeId;

        @Override
        public Call openCall(UUID callerId, UUID calleeId) {
            this.callerId = callerId;
            this.calleeId = calleeId;
            return new Call(UUID.randomUUID(), callerId, calleeId, ConnectionState.ALIVE);
        }

        @Override
        public Call closeCall(UUID id) {
            return new Call(id, callerId, calleeId, ConnectionState.DEAD);
        }

        @Override
        public Call closeCall(UUID callerId, UUID calleeId) {
            return new Call(UUID.randomUUID(), callerId, calleeId, ConnectionState.DEAD);
        }
    }

    private static class StubEntityPresenter implements EntityPresenter {

        @Override
        public void present(PresentableEntity entity) {}

        @Override
        public void present(List<PresentableEntity> entities) {}

        @Override
        public void presentError(ErrorEntity error) {}
    }

    private static class StubRequestPresenter implements CallRequestPresenter {

        @Override
        public void present(InverseCallRequest request) {
            System.out.println("Requesting calls : " + request);
        }
    }

    private static class StubErrorPresenter implements ErrorPresenter {

        @Override
        public void present(io.github.singlerr.semaphore.interactors.caller.presenter.data.Error error) {
            System.out.println("Error Presenter : " + error);
        }
    }

    private static class StubResponsePresenter implements CallResponsePresenter {

        @Override
        public void error(Error entity) {
            System.out.println("Response Error : " + entity);
        }

        @Override
        public void present(CallResponse entity) {
            System.out.println("Call Response : " + entity);
        }
    }

    private static class StubErrorHandler implements ErrorHandler {

        @Override
        public void error(Error entity) {
            System.out.println("Error Handler :  " + entity);
        }
    }

    private static class StubCallConnectionHandler implements CallConnectionHandler {

        private CallConnection cache;

        @Override
        public CallConnection open(UUID callerId, UUID calleeId) {
            return (cache = new CallConnection(UUID.randomUUID(), calleeId, callerId, CallState.ALIVE));
        }

        @Override
        public CallConnection close(UUID connectionId) {
            return new CallConnection(connectionId, cache.getCalleeId(), cache.getCallerId(), CallState.DEAD);
        }

        @Override
        public CallConnection getById(UUID connectionId) {
            return cache;
        }

        @Override
        public List<CallConnection> getAll() {
            return Collections.emptyList();
        }
    }
}
