/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.sound.ClientSoundHandler;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.ClientUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.*;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallEstablishedPacket extends Packet {

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

    public static class Handler extends PacketHandler<CallEstablishedPacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallEstablishedPacket packet) {
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallEstablishedPacket packet) {
            UUID playerId = ClientUtils.getClientUniqueId();
            PlayerContext callerCtx = packet.getCaller();
            PlayerContext calleeCtx = packet.getCallee();

            if (!calleeCtx.getOwner().equals(playerId) && !callerCtx.getOwner().equals(playerId)) return null;

            ClientRegistries.getPlayerState().setCallState(PlayerContext.CallState.IN_CALL);
            ClientSoundHandler.stopCallingSound();
            ClientSoundHandler.stopReceivingCallSound();
            ClientRegistries.getPhoneScreen().callEstablished();
            ClientSoundHandler.playCallEstablishedSound();
            return null;
        }
    }
}
