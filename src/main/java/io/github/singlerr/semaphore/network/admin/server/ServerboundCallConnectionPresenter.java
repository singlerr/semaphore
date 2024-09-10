/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server;

import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.packet.PacketErrorEntity;
import io.github.singlerr.semaphore.network.admin.packet.PacketPresentableCallConnection;
import net.minecraft.entity.player.EntityPlayerMP;

public final class ServerboundCallConnectionPresenter implements CallConnectionPresenter {

    private final NetworkManager networkManager;

    public ServerboundCallConnectionPresenter(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void present(PresentableCallConnection entity) {
        if (entity.getContext() instanceof EntityPlayerMP) {
            this.networkManager.sendTo(
                    new PacketPresentableCallConnection(
                            entity.id(), entity.callerId(), entity.calleeId(), entity.alive()),
                    (EntityPlayerMP) entity.getContext());
        }
    }

    @Override
    public void presentError(ErrorEntity error) {
        if (error.getContext() instanceof EntityPlayerMP) {
            this.networkManager.sendTo(new PacketErrorEntity(error.message()), (EntityPlayerMP) error.getContext());
        }
    }
}
