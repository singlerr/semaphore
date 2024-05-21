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
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

@Log4j2
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallRejectPacket extends Packet {

    private PlayerContext caller;

    private PlayerContext callee;

    private PlayerContext.CallRejectReason reason = PlayerContext.CallRejectReason.OTHER;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        PacketBuffer wrapper = new PacketBuffer(byteBuf);
        caller = PlayerContext.from(byteBuf);
        callee = PlayerContext.from(byteBuf);
        reason = wrapper.readEnumValue(PlayerContext.CallRejectReason.class);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        PacketBuffer wrapper = new PacketBuffer(byteBuf);
        caller.serialize(byteBuf);
        callee.serialize(byteBuf);
        wrapper.writeEnumValue(reason);
    }

    public static class Handler extends PacketHandler<CallRejectPacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallRejectPacket packet) {
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

                CallRejectPacket respPacket = CallRejectPacket.builder()
                        .caller(serverCaller)
                        .callee(serverCallee)
                        .reason(PlayerContext.CallRejectReason.PLAYER_NOT_ONLINE)
                        .build();

                if (calleePlayer != null) {
                    CommonRegistries.NETWORK.sendTo(respPacket, calleePlayer);
                }

                if (callerPlayer != null) {
                    CommonRegistries.NETWORK.sendTo(respPacket, callerPlayer);
                }

                return null;
            }

            // State check
            if (serverCaller.getCallState() != PlayerContext.CallState.CALLING) {
                log.info("Callee {} rejected caller {} but caller is not calling", serverCallee, serverCaller);
                serverCallee.setCallState(PlayerContext.CallState.IDLE);

                CallRejectPacket respPacket = CallRejectPacket.builder()
                        .caller(serverCaller)
                        .callee(serverCallee)
                        .reason(PlayerContext.CallRejectReason.OTHER)
                        .build();

                CommonRegistries.NETWORK.sendTo(respPacket, calleePlayer);
                return null;
            }

            if (serverCallee.getCallState() != PlayerContext.CallState.RECEIVING_CALL) {
                log.info("Callee {} rejected caller {} but callee is not receiving call", serverCallee, serverCaller);
                serverCaller.setCallState(PlayerContext.CallState.IDLE);
                CallRejectPacket respPacket = CallRejectPacket.builder()
                        .caller(serverCaller)
                        .callee(serverCallee)
                        .reason(PlayerContext.CallRejectReason.OTHER)
                        .build();

                CommonRegistries.NETWORK.sendTo(respPacket, callerPlayer);
                return null;
            }

            serverCaller.setCallState(PlayerContext.CallState.IDLE);
            serverCallee.setCallState(PlayerContext.CallState.IDLE);

            log.info("Callee {} rejected caller {}", serverCallee, serverCaller);

            CallRejectPacket respPacket = CallRejectPacket.builder()
                    .caller(serverCaller)
                    .callee(serverCallee)
                    .reason(PlayerContext.CallRejectReason.PLAYER_REJECTED)
                    .build();

            CommonRegistries.NETWORK.sendTo(respPacket, callerPlayer);
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallRejectPacket packet) {
            UUID playerId = ClientUtils.getClientUniqueId();

            PlayerContext caller = packet.getCaller();
            PlayerContext callee = packet.getCallee();

            if (!callee.getOwner().equals(playerId) && !caller.getOwner().equals(playerId)) return null;

            ClientSoundHandler.stopCallingSound();
            ClientSoundHandler.playRejectedBy(packet.getReason());
            ClientRegistries.getPhoneScreen().callClosed();
            return null;
        }
    }
}
