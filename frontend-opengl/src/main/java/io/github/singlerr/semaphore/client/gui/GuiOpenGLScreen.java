/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui;

import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import net.minecraft.client.gui.GuiScreen;

public final class GuiOpenGLScreen extends GuiScreen implements CallRequestPresenter, CallResponsePresenter {

    public GuiOpenGLScreen() {}

    @Override
    public void present(CallResponse entity) {}

    @Override
    public void error(Error entity) {}

    @Override
    public void present(InverseCallRequest request) {}
}
