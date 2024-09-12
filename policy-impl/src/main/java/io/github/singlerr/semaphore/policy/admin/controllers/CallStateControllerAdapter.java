/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.controllers;

import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery;
import io.github.singlerr.semaphore.interactors.admin.manager.CallStateManager;
import io.github.singlerr.semaphore.interactors.admin.manager.data.Call;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;

public final class CallStateControllerAdapter implements CallStateController {

    private final CallStateManager callStateManager;
    private final EntityPresenter entityPresenter;

    public CallStateControllerAdapter(CallStateManager callStateManager, EntityPresenter entityPresenter) {
        this.callStateManager = callStateManager;
        this.entityPresenter = entityPresenter;
    }

    @Override
    public void openCall(CallStateQuery.OpenCall query) {
        Call call = this.callStateManager.openCall(query.getCallerId(), query.getCalleeId());
        if (call == null) {
            entityPresenter.presentError(new ErrorEntity("error.open.call"));
            return;
        }
    }

    @Override
    public void closeCall(CallStateQuery.CloseCall query) {
        this.callStateManager.closeCall(query.getCallerId(), query.getCalleeId());
    }

    @Override
    public void closeCall(CallStateQuery.CloseCallById query) {
        this.callStateManager.closeCall(query.getId());
    }
}
