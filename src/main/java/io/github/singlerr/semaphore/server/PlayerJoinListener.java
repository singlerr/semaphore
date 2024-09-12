/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.server;

import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public final class PlayerJoinListener {

    private final EntityController entityController;

    public PlayerJoinListener(EntityController entityController) {
        this.entityController = entityController;
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        entityController.getAllEntities(new EntityQuery.GetAllEntities());
    }
}
