/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.NetworkUtils;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallAcceptPacket extends Packet {

    private PlayerContext caller;

    private PlayerContext callee;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.callee = PlayerContext.from(byteBuf);
        this.caller = PlayerContext.from(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        callee.serialize(byteBuf);
        caller.serialize(byteBuf);
    }

    @Log4j2
    public static class Handler extends PacketHandler<CallAcceptPacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallAcceptPacket packet) {
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
                CallRejectPacket respPacket = CallRejectPacket.builder()
                        .callee(serverCallee)
                        .caller(serverCaller)
                        .reason(PlayerContext.CallRejectReason.OTHER)
                        .build();
                serverCallee.setCallState(PlayerContext.CallState.IDLE);
                CommonRegistries.NETWORK.sendTo(respPacket, calleePlayer);
                return null;
            }

            if (serverCallee.getCallState() != PlayerContext.CallState.RECEIVING_CALL) {
                CallRejectPacket respPacket = CallRejectPacket.builder()
                        .caller(serverCaller)
                        .callee(serverCallee)
                        .reason(PlayerContext.CallRejectReason.OTHER)
                        .build();
                CommonRegistries.NETWORK.sendTo(respPacket, callerPlayer);
                return null;
            }

            serverCaller.setCallState(PlayerContext.CallState.IN_CALL);
            serverCallee.setCallState(PlayerContext.CallState.IN_CALL);

            serverCallee.setOpponent(serverCaller.getOwner());
            serverCaller.setOpponent(serverCallee.getOwner());

            CallEstablishedPacket callEstablishedPacket = CallEstablishedPacket.builder()
                    .callee(serverCallee)
                    .caller(serverCaller)
                    .build();

            NetworkUtils.sendTo(CommonRegistries.NETWORK, callEstablishedPacket, calleePlayer, callerPlayer);
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallAcceptPacket packet) {
            return null;
        }
    }
}
