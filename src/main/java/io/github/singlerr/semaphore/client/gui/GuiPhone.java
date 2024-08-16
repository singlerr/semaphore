/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui;

import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController;
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.ErrorHandler;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.policy.callee.presenters.CallPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.presenters.ErrorHandlerAdapter;
import io.github.singlerr.semaphore.policy.caller.presenters.CallRequestPresenterAdapter;
import io.github.singlerr.semaphore.policy.caller.presenters.ErrorPresenterAdapter;
import net.minecraft.client.gui.GuiScreen;

public final class GuiPhone extends GuiScreen implements ErrorHandler, CallResponsePresenter, CallRequestPresenter {

    private final CallRequestController requestController;
    private final CallResponseController responseController;

    public GuiPhone(CallRequestController requestController, CallResponseController responseController) {
        this.requestController = requestController;
        this.responseController = responseController;
    }

    @Override
    public void error(Error entity) {}

    @Override
    public void present(CallResponse entity) {}

    public boolean shouldPresent(ErrorHandlerAdapter.ErrorContext errorContext) {
        return false;
    }

    public boolean shouldPresent(ErrorPresenterAdapter.ErrorContext errorContext) {
        return false;
    }

    public boolean shouldPresent(CallPresenterAdapter.PresenterContext context) {
        return false;
    }

    public boolean shouldPresent(CallRequestPresenterAdapter.PresenterContext context) {
        return false;
    }

    @Override
    public void present(InverseCallRequest request) {

    }
}
