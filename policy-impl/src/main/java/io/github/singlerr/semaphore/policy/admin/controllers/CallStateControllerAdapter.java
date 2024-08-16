/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.controllers;

import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery;
import io.github.singlerr.semaphore.interactors.admin.manager.CallStateManager;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;

public final class CallStateControllerAdapter implements CallStateController {

    private final CallStateManager callStateManager;
    private final EntityPresenter entityPresenter;

    public CallStateControllerAdapter(CallStateManager callStateManager, EntityPresenter entityPresenter) {
        this.callStateManager = callStateManager;
        this.entityPresenter = entityPresenter;
    }

    @Override
    public void getCallState(CallStateQuery.GetCallState query) {
        int state = this.callStateManager.getById(query.id());
        if (state == -1) {
            this.entityPresenter.presentError(new ErrorEntity("call.state.not.found"));
            return;
        }

        this.entityPresenter.present(new PresentableEntity(query.id(), state));
    }

    @Override
    public void setCallState(CallStateQuery.SetCallState query) {
        this.callStateManager.updateById(query.id(), query.state());
    }
}
