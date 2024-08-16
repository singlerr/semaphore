/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.caller.interactors;

import static org.junit.jupiter.api.Assertions.*;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;
import io.github.singlerr.semaphore.interactors.caller.manager.data.CallRequest;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.policy.caller.SimpleCallerInteractor;
import io.github.singlerr.semaphore.policy.database.PlayerDatabase;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CallStateMachineTest {
    @Test
    void testRequest() {
        DatabaseGateway stubDatabase = new PlayerDatabase();
        CallerInteractor stubInteractor =
                new SimpleCallerInteractor(stubDatabase, new StubErrorHandler(), new StubResponsePresenter());

        Entity stubCaller = new Entity(UUID.randomUUID(), 0);
        Entity stubCallee = new Entity(UUID.randomUUID(), 0);

        stubDatabase.create(stubCaller.id(), stubCaller);
        stubDatabase.create(stubCallee.id(), stubCallee);

        stubInteractor.getCallRequestManager().request(new CallRequest(stubCaller.id(), stubCallee.id()));

        assertEquals(1, stubDatabase.getById(stubCaller.id()).stateId());
        assertEquals(2, stubDatabase.getById(stubCallee.id()).stateId());
    }

    private static class StubResponsePresenter implements CallRequestPresenter {

        @Override
        public void present(InverseCallRequest request) {
            System.out.println(request);
        }
    }

    private static class StubErrorHandler implements ErrorPresenter {

        @Override
        public void present(Error error) {
            System.out.println(error);
        }
    }
}
