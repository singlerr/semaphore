/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.controllers;

import io.github.singlerr.semaphore.interactors.admin.controller.CallConnectionController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallConnectionQuery;
import io.github.singlerr.semaphore.interactors.admin.manager.CallConnectionManager;
import io.github.singlerr.semaphore.interactors.admin.manager.data.CallConnectionEntity;
import io.github.singlerr.semaphore.interactors.admin.manager.data.ConnectionState;
import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;

public final class CallConnectionControllerAdapter implements CallConnectionController {

    private final CallConnectionManager callConnectionManager;
    private final CallConnectionPresenter callConnectionPresenter;

    public CallConnectionControllerAdapter(
            CallConnectionManager callConnectionManager, CallConnectionPresenter callConnectionPresenter) {
        this.callConnectionManager = callConnectionManager;
        this.callConnectionPresenter = callConnectionPresenter;
    }

    @Override
    public void openConnection(CallConnectionQuery.OpenConnection query) {
        this.callConnectionManager.open(query.callerId(), query.calleeId());
    }

    @Override
    public void closeConnection(CallConnectionQuery.CloseConnection query) {
        this.callConnectionManager.close(query.id());
    }

    @Override
    public void getConnection(CallConnectionQuery.GetConnection query) {
        CallConnectionEntity connection = this.callConnectionManager.getById(query.id());
        if (connection == null) {
            this.callConnectionPresenter.presentError(new ErrorEntity("call.connection.not.found"));
            return;
        }

        this.callConnectionPresenter.present(new PresentableCallConnection(
                connection.id(),
                connection.callerId(),
                connection.calleeId(),
                connection.state() == ConnectionState.ALIVE));
    }
}
