/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.callee.server;

import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.callee.packet.PacketCallResponse;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.server.FMLServerHandler;

public final class ServerboundCallResponsePresenter implements CallResponsePresenter {

    private final NetworkManager networkManager;

    public ServerboundCallResponsePresenter(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void present(CallResponse entity) {
        EntityPlayerMP player =
                FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(entity.callerId());
        if (player != null) {
            this.networkManager.sendTo(
                    new PacketCallResponse(
                            entity.callerId(),
                            entity.calleeId(),
                            entity.responseType() == CallResponse.ResponseType.ACCEPT
                                    ? io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse
                                    .Response.ACCEPT
                                    : io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse
                                    .Response.REJECT),
                    player);
        }
    }

    @Override
    public void error(Error entity) {
    }
}
