/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.server;

import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.interactors.admin.manager.CallConnectionManager;
import io.github.singlerr.semaphore.interactors.admin.manager.CallStateManager;
import io.github.singlerr.semaphore.interactors.admin.manager.data.CallConnectionEntity;
import java.util.Optional;
import java.util.UUID;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public final class PlayerEventListener {

    private final EntityController entityController;
    private final CallConnectionManager callConnectionManager;
    private CallStateManager callStateManager;

    public PlayerEventListener(
            EntityController entityController,
            CallStateManager callStateManager,
            CallConnectionManager callConnectionManager) {
        this.entityController = entityController;
        this.callStateManager = callStateManager;
        this.callConnectionManager = callConnectionManager;
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        entityController.getAllEntities(new EntityQuery.GetAllEntities());
    }

    @SubscribeEvent
    public void onQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.player.getUniqueID();
        Optional<CallConnectionEntity> conOpt = callConnectionManager.getAll().stream()
                .filter(c -> c.getCalleeId().equals(playerId) || c.getCallerId().equals(playerId))
                .findAny();
        if (conOpt.isPresent()) {
            CallConnectionEntity connection = conOpt.get();
            callStateManager.closeCall(connection.getId());
        }
    }
}
