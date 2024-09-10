/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callee;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.manager.CallStateManager;
import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.callee.manager.CallResponseManager;
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.ErrorHandler;
import io.github.singlerr.semaphore.policy.callee.interactors.CallStateMachine;

public final class SimpleCalleeInteractor implements CalleeInteractor {

    private final CallResponseManager callResponseManager;
    private final ErrorHandler errorPresenter;
    private final CallResponsePresenter responsePresenter;

    public SimpleCalleeInteractor(
            DatabaseGateway database,
            CallStateManager callStateManager,
            ErrorHandler errorPresenter,
            CallResponsePresenter responsePresenter) {
        this.callResponseManager = new CallStateMachine(database, callStateManager, responsePresenter);
        this.errorPresenter = errorPresenter;
        this.responsePresenter = responsePresenter;
    }

    @Override
    public CallResponseManager getResponseManager() {
        return callResponseManager;
    }

    @Override
    public ErrorHandler getErrorPresenter() {
        return errorPresenter;
    }

    @Override
    public CallResponsePresenter getResponsePresenter() {
        return responsePresenter;
    }
}
