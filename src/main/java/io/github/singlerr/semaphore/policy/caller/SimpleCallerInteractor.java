/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.caller;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;
import io.github.singlerr.semaphore.interactors.caller.manager.CallRequestManager;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.policy.caller.interactors.CallStateMachine;

public final class SimpleCallerInteractor implements CallerInteractor {

    private final DatabaseGateway database;
    private final CallRequestManager requestManager;
    private final ErrorPresenter errorPresenter;
    private final CallRequestPresenter requestPresenter;

    public SimpleCallerInteractor(
            DatabaseGateway database, ErrorPresenter errorPresenter, CallRequestPresenter requestPresenter) {
        this.database = database;
        this.requestManager = new CallStateMachine(database, errorPresenter, requestPresenter);
        this.errorPresenter = errorPresenter;
        this.requestPresenter = requestPresenter;
    }

    @Override
    public CallRequestManager getCallRequestManager() {
        return requestManager;
    }

    @Override
    public ErrorPresenter getErrorPresenter() {
        return errorPresenter;
    }

    @Override
    public CallRequestPresenter getCallRequestPresenter() {
        return requestPresenter;
    }
}
