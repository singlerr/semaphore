/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.caller.interactors;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.access.database.EntityType;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;
import io.github.singlerr.semaphore.interactors.caller.manager.data.CallRequest;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.policy.caller.SimpleCallerInteractor;
import io.github.singlerr.semaphore.policy.database.PlayerDatabase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CallStateMachineTest {
    @Test
    void testRequest() {
        DatabaseGateway stubDatabase = new PlayerDatabase();
        EntityPresenter stubEntityPresenter = new StubEntityPresenter();
        CallerInteractor stubInteractor = new SimpleCallerInteractor(
                stubDatabase, new StubErrorHandler(), new StubResponsePresenter(), stubEntityPresenter);

        Entity stubCaller = new Entity(UUID.randomUUID(), new Entity.State(0, new HashMap<>(), EntityType.PLAYER));
        Entity stubCallee = new Entity(UUID.randomUUID(), new Entity.State(0, new HashMap<>(), EntityType.PLAYER));

        stubDatabase.create(stubCaller.getId(), stubCaller);
        stubDatabase.create(stubCallee.getId(), stubCallee);

        stubInteractor.getCallRequestManager().request(new CallRequest(stubCaller.getId(), stubCallee.getId()));

        assertEquals(1, stubDatabase.getById(stubCaller.getId()).getState().getStateId());
        assertEquals(2, stubDatabase.getById(stubCallee.getId()).getState().getStateId());
    }

    @Test
    void testCancelCall() {
        DatabaseGateway stubDatabase = new PlayerDatabase();
        EntityPresenter stubEntityPresenter = new StubEntityPresenter();
        CallerInteractor stubInteractor = new SimpleCallerInteractor(
                stubDatabase, new StubErrorHandler(), new StubResponsePresenter(), stubEntityPresenter);

        Entity stubCaller = new Entity(UUID.randomUUID(), new Entity.State(0, new HashMap<>(), EntityType.PLAYER));
        Entity stubCallee = new Entity(UUID.randomUUID(), new Entity.State(0, new HashMap<>(), EntityType.PLAYER));

        stubDatabase.create(stubCaller.getId(), stubCaller);
        stubDatabase.create(stubCallee.getId(), stubCallee);

        stubInteractor.getCallRequestManager().request(new CallRequest(stubCaller.getId(), stubCallee.getId()));

        assertEquals(1, stubDatabase.getById(stubCaller.getId()).getState().getStateId());
        assertEquals(2, stubDatabase.getById(stubCallee.getId()).getState().getStateId());

        // Then cancel calls - request twice
        stubInteractor.getCallRequestManager().request(new CallRequest(stubCaller.getId(), stubCallee.getId()));

        assertEquals(0, stubDatabase.getById(stubCaller.getId()).getState().getStateId());
        assertEquals(0, stubDatabase.getById(stubCallee.getId()).getState().getStateId());
    }

    private static class StubResponsePresenter implements CallRequestPresenter {

        @Override
        public void present(InverseCallRequest request) {
            System.out.println("Requesting calls : " + request);
        }
    }

    private static class StubErrorHandler implements ErrorPresenter {

        @Override
        public void present(Error error) {
            System.out.println("Error Presenter : " + error);
        }
    }

    private static class StubEntityPresenter implements EntityPresenter {

        @Override
        public void present(PresentableEntity entity) {
        }

        @Override
        public void present(List<PresentableEntity> entities) {
        }

        @Override
        public void presentError(ErrorEntity error) {
        }
    }
}
