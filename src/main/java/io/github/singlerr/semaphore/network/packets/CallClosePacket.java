/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.sound.ClientSoundHandler;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.ClientUtils;
import io.github.singlerr.semaphore.utils.NetworkUtils;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

@Log4j2
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallClosePacket extends Packet {

    private PlayerContext caller;

    private PlayerContext callee;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.caller = PlayerContext.from(byteBuf);
        this.callee = PlayerContext.from(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        caller.serialize(byteBuf);
        callee.serialize(byteBuf);
    }

    public static class Handler extends PacketHandler<CallClosePacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallClosePacket packet) {
            UUID requesterID = ctx.getServerHandler().player.getUniqueID();

            PlayerContext caller = packet.getCaller();
            PlayerContext callee = packet.getCallee();

            Optional<PlayerContext> serverCallerState =
                    ServerRegistries.getStatePool().get(caller.getOwner(), PlayerContext.class);
            Optional<PlayerContext> serverCalleeState =
                    ServerRegistries.getStatePool().get(callee.getOwner(), PlayerContext.class);

            if (!serverCallerState.isPresent() || !serverCalleeState.isPresent()) {
                log.warn("Server state not found for caller: {} or callee: {}", caller.getOwner(), callee.getOwner());
                return null;
            }
            PlayerContext serverCaller = serverCallerState.get();
            PlayerContext serverCallee = serverCalleeState.get();

            EntityPlayerMP callerPlayer =
                    FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(caller.getOwner());
            EntityPlayerMP calleePlayer =
                    FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(callee.getOwner());

            if (calleePlayer == null || callerPlayer == null) {
                log.warn("Caller or callee player is not online");

                if (calleePlayer != null) {
                    CommonRegistries.NETWORK.sendTo(packet, calleePlayer);
                }

                if (callerPlayer != null) {
                    CommonRegistries.NETWORK.sendTo(packet, callerPlayer);
                }

                return null;
            }

            if (serverCaller.getCallState() != PlayerContext.CallState.IN_CALL
                    || serverCallee.getCallState() != PlayerContext.CallState.IN_CALL) {
                log.warn("Peer requested to close call but caller {} is not in call", serverCaller);
                if (serverCaller.getOwner().equals(requesterID)) {
                    serverCaller.setCallState(PlayerContext.CallState.IDLE);
                    CommonRegistries.NETWORK.sendTo(packet, callerPlayer);
                } else {
                    serverCallee.setCallState(PlayerContext.CallState.IDLE);
                    CommonRegistries.NETWORK.sendTo(packet, calleePlayer);
                }
            }

            serverCallee.setCallState(PlayerContext.CallState.IDLE);
            serverCaller.setCallState(PlayerContext.CallState.IDLE);

            NetworkUtils.sendTo(CommonRegistries.NETWORK, packet, callerPlayer, calleePlayer);
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallClosePacket packet) {
            UUID playerId = ClientUtils.getClientUniqueId();

            PlayerContext caller = packet.getCaller();
            PlayerContext callee = packet.getCallee();

            if (!caller.getOwner().equals(playerId) && !callee.getOwner().equals(playerId)) return null;

            ClientRegistries.getPlayerState().setCallState(PlayerContext.CallState.IDLE);
            ClientRegistries.getPhoneScreen().callClosed();
            ClientSoundHandler.playCallClosedSound();
            return null;
        }
    }
}
