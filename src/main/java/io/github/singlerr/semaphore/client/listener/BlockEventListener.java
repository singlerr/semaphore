/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.listener;

import io.github.singlerr.semaphore.block.entity.TileEntityPhoneBox;
import io.github.singlerr.semaphore.client.ClientSideEntity;
import io.github.singlerr.semaphore.client.ClientSideEntityCache;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.access.database.EntityType;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController;
import io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse;
import io.github.singlerr.semaphore.policy.dfa.PlayerState;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class BlockEventListener {

    private final EntityController entityController;
    private final CallResponseController responseController;

    public BlockEventListener(EntityController entityController, CallResponseController responseController) {
        this.entityController = entityController;
        this.responseController = responseController;
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        UUID playerId = event.getEntityPlayer().getUniqueID();
        if (!(event.getWorld().getTileEntity(pos) instanceof TileEntityPhoneBox)) return;

        TileEntityPhoneBox phoneBox = (TileEntityPhoneBox) event.getWorld().getTileEntity(pos);
        if (phoneBox.getState() != null && phoneBox.getState() == PlayerState.RECEIVING_CALL) {
            ClientSideEntity cachedEntity =
                    ClientResources.getInstance(ClientSideEntityCache.class).getEntity();
            if (cachedEntity == null)
                cachedEntity = new ClientSideEntity(
                        new Entity(playerId, new Entity.State(0, new HashMap<>(), EntityType.PLAYER)));
            entityController.updateEntity(new EntityQuery.UpdateEntity(
                    playerId,
                    new EntityQuery.State(
                            2,
                            cachedEntity.getEntity().getState().getMissCallCount(),
                            io.github.singlerr.semaphore.interactors.admin.controller.data.EntityType.valueOf(
                                    cachedEntity
                                            .getEntity()
                                            .getState()
                                            .getEntityType()
                                            .name()))));
            entityController.updateEntity(new EntityQuery.UpdateEntity(
                    phoneBox.getId(),
                    new EntityQuery.State(
                            0,
                            new HashMap<>(),
                            io.github.singlerr.semaphore.interactors.admin.controller.data.EntityType.PHONE_BOX)));
            responseController.reply(new CallResponse(phoneBox.getCallerId(), playerId, CallResponse.Response.ACCEPT));
        }
    }
}
