/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.server;

import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.caller.packet.PacketError;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.server.FMLServerHandler;

public class ServerboundErrorPresenter implements ErrorPresenter {

    private final NetworkManager networkManager;

    public ServerboundErrorPresenter(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void present(Error error) {
        EntityPlayerMP caller =
                FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(error.getCallerId());
        EntityPlayerMP callee =
                FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(error.getCalleeId());

        PacketError packet = new PacketError(error.getCallerId(), error.getCalleeId(), error.getReason());

        if (caller != null) {
            this.networkManager.sendTo(packet, caller);
        }
        if (callee != null) {
            this.networkManager.sendTo(packet, callee);
        }
    }
}
