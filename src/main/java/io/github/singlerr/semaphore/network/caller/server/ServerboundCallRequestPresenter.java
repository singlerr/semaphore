/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.server;

import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.caller.packet.PacketInverseCallRequest;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.server.FMLServerHandler;

public final class ServerboundCallRequestPresenter implements CallRequestPresenter {

    private final NetworkManager networkManager;

    public ServerboundCallRequestPresenter(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void present(InverseCallRequest request) {
        EntityPlayerMP player =
                FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(request.getCalleeId());
        PacketInverseCallRequest packet = new PacketInverseCallRequest(request.getCallerId(), request.getCalleeId());
        if (player != null) {
            this.networkManager.sendTo(packet, player);
        } else {
            this.networkManager.sendToAll(packet);
        }
    }
}
