/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.listener;

import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class GuiEventListener {

    private final EntityController entityController;

    public GuiEventListener(EntityController entityController) {
        this.entityController = entityController;
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof EntityPresenter) {
            entityController.getAllEntities(new EntityQuery.GetAllEntities());
        }
    }
}
