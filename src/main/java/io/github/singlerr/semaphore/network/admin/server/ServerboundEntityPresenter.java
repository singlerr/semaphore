/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server;

import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.EntityType;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.packet.PacketEntityErrorEntity;
import io.github.singlerr.semaphore.network.admin.packet.PacketPresentableEntities;
import io.github.singlerr.semaphore.network.admin.packet.PacketPresentableEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ServerboundEntityPresenter implements EntityPresenter {

    private final NetworkManager networkManager;

    public ServerboundEntityPresenter(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void present(PresentableEntity entity) {
        PacketPresentableEntity packet = new PacketPresentableEntity(
                entity.getId(),
                entity.getState().getStateId(),
                entity.getState().getMissCallCount(),
                EntityType.valueOf(entity.getState().getEntityType().name()));
        if (entity.getContext() instanceof EntityPlayerMP) {
            this.networkManager.sendTo(packet, (EntityPlayerMP) entity.getContext());
        } else {
            this.networkManager.sendToAll(packet);
        }
    }

    @Override
    public void presentError(ErrorEntity error) {
        if (error.getContext() instanceof Map.Entry) {
            PacketEntityErrorEntity packet = new PacketEntityErrorEntity(error.getMessage());

            Map.Entry<UUID, UUID> peer = (Map.Entry<UUID, UUID>) error.getContext();

            EntityPlayerMP player =
                    FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(peer.getKey());
            if (player != null) {
                this.networkManager.sendTo(packet, player);
            }

            player = FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(peer.getValue());
            if (player != null) {
                this.networkManager.sendTo(packet, player);
            }
        }
        if (error.getContext() instanceof EntityPlayerMP) {
            this.networkManager.sendTo(
                    new PacketEntityErrorEntity(error.getMessage()), (EntityPlayerMP) error.getContext());
        }
    }

    @Override
    public void present(List<PresentableEntity> entities) {
        if (entities.isEmpty()) return;

        PacketPresentableEntities packet = new PacketPresentableEntities(entities);

        Object context = entities.get(0).getContext();
        if (context instanceof EntityPlayerMP) {
            this.networkManager.sendTo(packet, (EntityPlayerMP) context);
        } else {
            this.networkManager.sendToAll(packet);
        }
    }
}
