/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import io.github.singlerr.semaphore.network.Packet;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

@UtilityClass
@SideOnly(Side.SERVER)
public class NetworkUtils {
    public void sendTo(SimpleNetworkWrapper network, Packet packet, Predicate<EntityPlayerMP> filter) {
        for (EntityPlayerMP player :
                FMLServerHandler.instance().getServer().getPlayerList().getPlayers()) {
            if (filter.test(player)) {
                network.sendTo(packet, player);
            }
        }
    }

    public void sendToIgnoreSender(SimpleNetworkWrapper network, EntityPlayerMP sender, Packet packet) {
        sendTo(network, packet, (p) -> !p.getUniqueID().equals(sender.getUniqueID()));
    }

    public void sendTo(SimpleNetworkWrapper network, Packet packet, EntityPlayerMP... players) {
        for (EntityPlayerMP player : players) {
            network.sendTo(packet, player);
        }
    }
}
