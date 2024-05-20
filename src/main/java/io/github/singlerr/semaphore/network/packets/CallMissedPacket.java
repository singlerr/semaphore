/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallMissedPacket extends Packet {

    private PlayerContext caller;

    private boolean doResetCount;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        caller = PlayerContext.from(byteBuf);
        doResetCount = byteBuf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        caller.serialize(byteBuf);
        byteBuf.writeBoolean(doResetCount);
    }

    public static class Handler extends PacketHandler<CallMissedPacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallMissedPacket packet) {
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallMissedPacket packet) {
            PlayerContext playerContext = ClientRegistries.getPlayerState();
            if (packet.isDoResetCount()) {
                playerContext.getMissCount(packet.getCaller().getOwner()).set(0);
            } else {
                playerContext.getMissCount(packet.getCaller().getOwner()).incrementAndGet();
            }

            ClientRegistries.getPhoneScreen().addOrUpdatePlayerState(packet.getCaller());
            return null;
        }
    }
}
