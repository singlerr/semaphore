/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server;

import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.packet.PacketErrorEntity;
import io.github.singlerr.semaphore.network.admin.packet.PacketPresentableEntities;
import io.github.singlerr.semaphore.network.admin.packet.PacketPresentableEntity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public final class ServerboundEntityPresenter implements EntityPresenter {

    private final NetworkManager networkManager;

    public ServerboundEntityPresenter(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void present(PresentableEntity entity) {
        if(entity.getContext() instanceof EntityPlayerMP){
            this.networkManager.sendTo(new PacketPresentableEntity(entity.id(), entity.state()), (EntityPlayerMP) entity.getContext());
        }
    }

    @Override
    public void presentError(ErrorEntity error) {
        if(error.getContext() instanceof EntityPlayerMP){
            this.networkManager.sendTo(new PacketErrorEntity(error.message()), (EntityPlayerMP) error.getContext());
        }
    }

    @Override
    public void present(List<PresentableEntity> entities) {
        if(entities.isEmpty())
            return;

        Object context = entities.get(0).getContext();
        if(context instanceof EntityPlayerMP){
            this.networkManager.sendTo(new PacketPresentableEntities(entities), (EntityPlayerMP) context);
        }
    }
}
