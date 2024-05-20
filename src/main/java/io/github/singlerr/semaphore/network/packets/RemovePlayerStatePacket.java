/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.NetworkUtils;
import io.netty.buffer.ByteBuf;
import lombok.*;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemovePlayerStatePacket extends Packet {

    private PlayerContext playerState;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.playerState = PlayerContext.from(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        playerState.serialize(byteBuf);
    }

    public static class Handler extends PacketHandler<RemovePlayerStatePacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, RemovePlayerStatePacket packet) {
            PlayerContext context = packet.getPlayerState();
            ServerRegistries.getStatePool().remove(context.getOwner());
            NetworkUtils.sendToIgnoreSender(CommonRegistries.NETWORK, ctx.getServerHandler().player, packet);
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, RemovePlayerStatePacket packet) {
            PlayerContext context = packet.getPlayerState();
            ClientRegistries.getPhoneScreen().removePlayerState(context);
            return null;
        }
    }
}
