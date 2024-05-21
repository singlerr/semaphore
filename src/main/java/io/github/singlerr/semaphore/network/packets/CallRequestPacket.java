/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.gui.NotificationWindow;
import io.github.singlerr.semaphore.gui.PhoneScreen;
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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

@Log4j2
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallRequestPacket extends Packet {

    private PlayerContext caller;

    private PlayerContext callee;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        caller = PlayerContext.from(byteBuf);
        callee = PlayerContext.from(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        caller.serialize(byteBuf);
        callee.serialize(byteBuf);
    }

    public static class Handler extends PacketHandler<CallRequestPacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallRequestPacket packet) {
            PlayerContext caller = packet.getCaller();
            PlayerContext callee = packet.getCallee();

            Optional<PlayerContext> serverCallerState =
                    ServerRegistries.getStatePool().get(caller.getOwner(), PlayerContext.class);
            Optional<PlayerContext> serverCalleeState =
                    ServerRegistries.getStatePool().get(callee.getOwner(), PlayerContext.class);

            if (!serverCallerState.isPresent() || !serverCalleeState.isPresent()) {
                log.warn("Server state not found for caller: {} or callee: {}", caller.getOwner(), callee.getOwner());
                CallRejectPacket respPacket = CallRejectPacket.builder()
                        .caller(caller)
                        .callee(callee)
                        .reason(PlayerContext.CallRejectReason.PLAYER_NOT_ONLINE)
                        .build();
                return respPacket;
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

                if (callerPlayer != null) {
                    CommonRegistries.NETWORK.sendTo(respPacket, callerPlayer);
                }

                return null;
            }

            if (serverCaller.getCallState() != PlayerContext.CallState.IDLE) {
                log.warn("Caller {} requested to call but call state is not idle", serverCaller);

                CallRejectPacket respPacket = CallRejectPacket.builder()
                        .caller(serverCaller)
                        .callee(serverCallee)
                        .reason(PlayerContext.CallRejectReason.OTHER)
                        .build();

                serverCaller.setCallState(PlayerContext.CallState.IDLE);
                CommonRegistries.NETWORK.sendTo(respPacket, callerPlayer);
                return null;
            }

            if (serverCallee.getCallState() != PlayerContext.CallState.IDLE) {
                log.warn(
                        "Caller {} requested to call to {} but callee is not in idle state",
                        serverCaller,
                        serverCallee);

                CallRejectPacket respPacket = CallRejectPacket.builder()
                        .caller(serverCaller)
                        .callee(serverCallee)
                        .reason(PlayerContext.CallRejectReason.PLAYER_IN_CALL)
                        .build();

                CallMissedPacket missedPacket = CallMissedPacket.builder()
                        .caller(serverCaller)
                        .doResetCount(false)
                        .build();
                serverCaller.setCallState(PlayerContext.CallState.IDLE);
                CommonRegistries.NETWORK.sendTo(respPacket, callerPlayer);
                CommonRegistries.NETWORK.sendTo(missedPacket, calleePlayer);
                return null;
            }

            serverCaller.setCallState(PlayerContext.CallState.CALLING);
            serverCallee.setCallState(PlayerContext.CallState.RECEIVING_CALL);

            CommonRegistries.NETWORK.sendTo(packet, calleePlayer);

            //            ServerRegistries.getTaskScheduler()
            //                    .schedule(
            //                            () -> {
            //                                checkMissCall(serverCaller.getOwner(), serverCallee.getOwner());
            //                            },
            //                            (long) (ModConfig.callTimeout * 1000),
            //                            TimeUnit.MILLISECONDS);
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallRequestPacket packet) {
            UUID playerId = ClientUtils.getClientUniqueId();

            if (!packet.getCallee().getOwner().equals(playerId)) return null;


            ClientRegistries.getPlayerState().setOpponent(packet.getCaller().getOwner());
            NotificationWindow window = ClientRegistries.getOrCreateNotificationWindow(ClientRegistries.getPlayerState().getOpponent());
            ClientRegistries.getPlayerState().setCallState(PlayerContext.CallState.RECEIVING_CALL);
            ClientSoundHandler.playReceivingCallSound();
            ClientRegistries.getPhoneScreen().receivingCall(packet.getCaller());

            if(! (Minecraft.getMinecraft().currentScreen instanceof PhoneScreen)){
                synchronized (window){
                    window.onShow();
                }
            }
            return null;
        }

        private void checkMissCall(UUID caller, UUID callee) {
            Optional<PlayerContext> serverCallerState =
                    ServerRegistries.getStatePool().get(caller, PlayerContext.class);
            Optional<PlayerContext> serverCalleeState =
                    ServerRegistries.getStatePool().get(callee, PlayerContext.class);

            if (!serverCallerState.isPresent() || !serverCalleeState.isPresent()) {
                log.warn("Server state not found for caller: {} or callee: {}", caller, callee);
                return;
            }

            PlayerContext serverCaller = serverCallerState.get();
            PlayerContext serverCallee = serverCalleeState.get();

            EntityPlayerMP callerPlayer =
                    FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(caller);
            EntityPlayerMP calleePlayer =
                    FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(callee);

            if (calleePlayer == null || callerPlayer == null) {
                log.warn("Caller or callee player is not online, closing call");

                CallRejectPacket respPacket = CallRejectPacket.builder()
                        .caller(serverCaller)
                        .callee(serverCallee)
                        .reason(PlayerContext.CallRejectReason.PLAYER_NOT_ONLINE)
                        .build();

                serverCallee.setCallState(PlayerContext.CallState.IDLE);
                serverCaller.setCallState(PlayerContext.CallState.IDLE);
                if (callerPlayer != null) {
                    CommonRegistries.NETWORK.sendTo(respPacket, callerPlayer);
                }
                return;
            }

            if (serverCaller.getCallState() != PlayerContext.CallState.CALLING
                    || serverCallee.getCallState() != PlayerContext.CallState.RECEIVING_CALL) {
                CallMissedPacket missedPacket = CallMissedPacket.builder()
                        .caller(serverCallee)
                        .doResetCount(true)
                        .build();
                CommonRegistries.NETWORK.sendTo(missedPacket, callerPlayer);
                return;
            }

            CallRejectPacket respPacket = CallRejectPacket.builder()
                    .caller(serverCaller)
                    .callee(serverCallee)
                    .reason(PlayerContext.CallRejectReason.PLAYER_NOT_ONLINE)
                    .build();

            serverCaller.setCallState(PlayerContext.CallState.IDLE);
            serverCallee.setCallState(PlayerContext.CallState.IDLE);

            CallClosePacket closePacket = CallClosePacket.builder()
                    .callee(serverCallee)
                    .caller(serverCaller)
                    .build();

            CallMissedPacket missedPacket = CallMissedPacket.builder()
                    .caller(serverCaller)
                    .doResetCount(false)
                    .build();

            CommonRegistries.NETWORK.sendTo(respPacket, callerPlayer);
            CommonRegistries.NETWORK.sendTo(closePacket, calleePlayer);
            CommonRegistries.NETWORK.sendTo(missedPacket, calleePlayer);
        }
    }
}
