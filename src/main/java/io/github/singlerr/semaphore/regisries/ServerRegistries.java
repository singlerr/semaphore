/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.regisries;

import io.github.singlerr.semaphore.network.packets.CallFeedbackPacket;
import io.github.singlerr.semaphore.network.packets.CallStatePacket;
import io.github.singlerr.semaphore.network.packets.PlayerStatePacket;
import io.github.singlerr.semaphore.network.wrapper.PacketWrapper;
import io.github.singlerr.semaphore.utils.EventPool;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ServerRegistries {
    public static void apply(FMLPreInitializationEvent event) {
        EventPool pool = CommonRegistries.getEventPool();
        pool.subscribe(PlayerStatePacket.Wrapper.class, ServerRegistries::updatePlayerState);
        pool.subscribe(CallStatePacket.Wrapper.class, ServerRegistries::updateCallState);
        pool.subscribe(CallFeedbackPacket.Wrapper.class, ServerRegistries::updateCallFeedback);
    }

    public static void apply(FMLInitializationEvent event) {}

    public static void apply(FMLPostInitializationEvent event) {}

    // Mod structure ensures that this method must be called on server side.
    private static void updatePlayerState(PacketWrapper<PlayerStatePacket> packet) {
        PlayerStatePacket statePacket = packet.getPacket();
        for (EntityPlayerMP otherPlayer :
                FMLServerHandler.instance().getServer().getPlayerList().getPlayers()) {
            if (otherPlayer.getUniqueID().equals(statePacket.getId())) continue;

            CommonRegistries.NETWORK.sendTo(statePacket, otherPlayer);
        }
    }

    private static void updateCallState(PacketWrapper<CallStatePacket> packet) {
        EntityPlayerMP player = packet.getContext().getServerHandler().player;
        CallStatePacket statePacket = packet.getPacket();
        for (EntityPlayerMP otherPlayer :
                FMLServerHandler.instance().getServer().getPlayerList().getPlayers()) {
            if (otherPlayer.getUniqueID().equals(player.getUniqueID())) continue;

            CommonRegistries.NETWORK.sendTo(statePacket, otherPlayer);
        }
    }

    private static void updateCallFeedback(PacketWrapper<CallFeedbackPacket> packet) {
        EntityPlayerMP player = packet.getContext().getServerHandler().player;
        CallFeedbackPacket statePacket = packet.getPacket();
        for (EntityPlayerMP otherPlayer :
                FMLServerHandler.instance().getServer().getPlayerList().getPlayers()) {
            if (otherPlayer.getUniqueID().equals(player.getUniqueID())) continue;

            CommonRegistries.NETWORK.sendTo(statePacket, otherPlayer);
        }
    }
}
