/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callee.interactors;

import static org.junit.jupiter.api.Assertions.*;

import io.github.singlerr.semaphore.interactors.access.call.CallConnection;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.call.CallState;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.callee.manager.data.ResponseType;
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.ErrorHandler;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.policy.callee.SimpleCalleeInteractor;
import io.github.singlerr.semaphore.policy.database.PlayerDatabase;
import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CallStateMachineTest {

    @Test
    void testAcceptCall() {
        DatabaseGateway stubDatabase = new PlayerDatabase();
        CallConnectionHandler stubCallConnectionHandler = new StubCallConnectionHandler();
        CalleeInteractor stubInteractor = new SimpleCalleeInteractor(
                stubDatabase, stubCallConnectionHandler, new StubErrorHandler(), new StubResponsePresenter());

        Entity stubCaller = new Entity(UUID.randomUUID(), new Entity.State(1, new HashMap<>()));
        Entity stubCallee = new Entity(UUID.randomUUID(), new Entity.State(2, new HashMap<>()));

        stubDatabase.create(stubCaller.id(), stubCaller);
        stubDatabase.create(stubCallee.id(), stubCallee);

        stubInteractor.getResponseManager().reply(stubCaller.id(), stubCallee.id(), ResponseType.ACCEPT);

        assertEquals(3, stubDatabase.getById(stubCaller.id()).state().stateId());
        assertEquals(3, stubDatabase.getById(stubCallee.id()).state().stateId());
    }

    @Test
    void testRejectCall() {
        DatabaseGateway stubDatabase = new PlayerDatabase();
        CallConnectionHandler stubCallConnectionHandler = new StubCallConnectionHandler();
        CalleeInteractor stubInteractor = new SimpleCalleeInteractor(
                stubDatabase, stubCallConnectionHandler, new StubErrorHandler(), new StubResponsePresenter());

        Entity stubCaller = new Entity(UUID.randomUUID(), new Entity.State(1, new HashMap<>()));
        Entity stubCallee = new Entity(UUID.randomUUID(), new Entity.State(2, new HashMap<>()));

        stubDatabase.create(stubCaller.id(), stubCaller);
        stubDatabase.create(stubCallee.id(), stubCallee);

        stubInteractor.getResponseManager().reply(stubCaller.id(), stubCallee.id(), ResponseType.REJECT);

        assertEquals(0, stubDatabase.getById(stubCaller.id()).state().stateId());
        assertEquals(0, stubDatabase.getById(stubCallee.id()).state().stateId());
    }

    @Test
    void testWhenPlayerNotAvailable() {
        DatabaseGateway stubDatabase = new PlayerDatabase();
        CallConnectionHandler stubCallConnectionHandler = new StubCallConnectionHandler();
        CalleeInteractor stubInteractor = new SimpleCalleeInteractor(
                stubDatabase, stubCallConnectionHandler, new StubErrorHandler(), new StubResponsePresenter());

        Entity stubCaller = new Entity(UUID.randomUUID(), new Entity.State(1, new HashMap<>()));
        Entity stubCallee = new Entity(UUID.randomUUID(), new Entity.State(2, new HashMap<>()));

        stubDatabase.create(stubCaller.id(), stubCaller);

        stubInteractor.getResponseManager().reply(stubCaller.id(), stubCallee.id(), ResponseType.REJECT);

        assertEquals(0, stubDatabase.getById(stubCaller.id()).state().stateId());
    }

    private static class StubResponsePresenter implements CallResponsePresenter {

        @Override
        public void error(Error entity) {
            System.out.println(entity.reason());
        }

        @Override
        public void present(CallResponse entity) {
            System.out.println(entity.responseType());
        }
    }

    private static class StubErrorHandler implements ErrorHandler {

        @Override
        public void error(Error entity) {
            System.out.println(entity.reason());
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
            return new CallConnection(connectionId, cache.calleeId(), cache.callerId(), CallState.DEAD);
        }

        @Override
        public CallConnection getById(UUID connectionId) {
            return cache;
        }
    }
}
