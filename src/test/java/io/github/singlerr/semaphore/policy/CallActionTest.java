package io.github.singlerr.semaphore.policy;

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
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;
import io.github.singlerr.semaphore.interactors.caller.manager.data.CallRequest;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.policy.callee.interactors.SimpleCalleeInteractor;
import io.github.singlerr.semaphore.policy.caller.interactors.SimpleCallerInteractor;
import io.github.singlerr.semaphore.policy.database.PlayerDatabase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CallActionTest {

    @Test
    void testRequestCallAndAccept(){
        DatabaseGateway stubDatabase = new PlayerDatabase();
        CallConnectionHandler stubCallConnectionHandler = new StubCallConnectionHandler();
        CalleeInteractor stubCalleeInteractor = new SimpleCalleeInteractor(
                stubDatabase, stubCallConnectionHandler, new StubErrorHandler(), new StubResponsePresenter());
        CallerInteractor stubCallerInteractor = new SimpleCallerInteractor(stubDatabase, new StubErrorPresenter(), new StubRequestPresenter());

        Entity stubCaller = new Entity(UUID.randomUUID(), 0);
        Entity stubCallee = new Entity(UUID.randomUUID(), 0);

        stubDatabase.create(stubCaller.id(), stubCaller);
        stubDatabase.create(stubCallee.id(), stubCallee);

        // 1. Request call
        stubCallerInteractor.getCallRequestManager().request(new CallRequest(stubCaller.id(), stubCallee.id()));

        assertEquals(1, stubDatabase.getById(stubCaller.id()).stateId());
        assertEquals(2, stubDatabase.getById(stubCallee.id()).stateId());

        // 2. Accept call
        stubCalleeInteractor.getResponseManager().reply(stubCaller.id(), stubCallee.id(), ResponseType.ACCEPT);

        assertEquals(3, stubDatabase.getById(stubCaller.id()).stateId());
        assertEquals(3, stubDatabase.getById(stubCallee.id()).stateId());

        // 3. Reset and reject call
        stubDatabase.update(stubCaller.id(), new Entity(stubCaller.id(), 0));
        stubDatabase.update(stubCallee.id(), new Entity(stubCallee.id(), 0));

        stubCallerInteractor.getCallRequestManager().request(new CallRequest(stubCaller.id(), stubCallee.id()));

        stubCalleeInteractor.getResponseManager().reply(stubCaller.id(), stubCallee.id(), ResponseType.REJECT);

        assertEquals(0, stubDatabase.getById(stubCaller.id()).stateId());
        assertEquals(0, stubDatabase.getById(stubCallee.id()).stateId());
    }

    private class StubRequestPresenter implements CallRequestPresenter {

        @Override
        public void present(InverseCallRequest request) {
            System.out.println(request);
        }
    }

    private class StubErrorPresenter implements ErrorPresenter {

        @Override
        public void present(io.github.singlerr.semaphore.interactors.caller.presenter.data.Error error) {
            System.out.println(error);
        }
    }

    private class StubResponsePresenter implements CallResponsePresenter {

        @Override
        public void error(Error entity) {
            System.out.println(entity.reason());
        }

        @Override
        public void present(CallResponse entity) {
            System.out.println(entity.responseType());
        }
    }

    private class StubErrorHandler implements ErrorHandler {

        @Override
        public void error(Error entity) {
            System.out.println(entity.reason());
        }
    }

    private class StubCallConnectionHandler implements CallConnectionHandler {

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
