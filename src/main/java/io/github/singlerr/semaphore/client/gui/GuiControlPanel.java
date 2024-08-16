/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.gui;

import io.github.singlerr.semaphore.interactors.admin.controller.CallConnectionController;
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.policy.admin.presenters.CallConnectionPresenterAdapter;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public final class GuiControlPanel extends GuiScreen implements CallConnectionPresenter, EntityPresenter {

    private final EntityController entityController;
    private final CallConnectionController callConnectionController;
    private final CallStateController callStateController;

    public GuiControlPanel(
            EntityController entityController,
            CallConnectionController callConnectionController,
            CallStateController callStateController) {
        this.entityController = entityController;
        this.callConnectionController = callConnectionController;
        this.callStateController = callStateController;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    public void onGuiOpened(){
        entityController.getAllEntities(new EntityQuery.GetAllEntities());
    }

    @Override
    public void present(List<PresentableEntity> entities) {

    }

    @Override
    public void present(PresentableCallConnection entity) {}

    @Override
    public void present(PresentableEntity entity) {}

    @Override
    public void presentError(ErrorEntity error) {}

    public boolean shouldPresent(CallConnectionPresenterAdapter.PresenterContext context) {
        return false;
    }

    public boolean shouldPresent(EntityPresenterAdapter.PresenterContext context) {
        return false;
    }
}
