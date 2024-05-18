/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.events.PlayerStateChangeEvent;
import io.github.singlerr.semaphore.events.RemovePlayerStateEvent;
import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.network.wrapper.PacketWrapper;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class PlayerStatePacket extends Packet {

    private PlayerContext state;

    private UUID id;

    private PlayerContext.PlayerStateAction action = PlayerContext.PlayerStateAction.CREATE_OR_UPDATE;

    @Override
    public void fromBytes(ByteBuf buf) {
        id = SerializationUtils.readUUID(buf);
        action = PlayerContext.PlayerStateAction.values()[buf.readInt()];
        state = PlayerContext.builder().build();
        state.deserialize(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        SerializationUtils.writeUUID(buf, id);
        if (action == null) action = PlayerContext.PlayerStateAction.CREATE_OR_UPDATE;
        buf.writeInt(action.ordinal());
        state.serialize(buf);
    }

    @NoArgsConstructor
    public static class Handler extends PacketHandler<PlayerStatePacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, PlayerStatePacket packet) {
            PlayerStateChangeEvent event = new PlayerStateChangeEvent(packet.getState());
            ServerRegistries.getEventPool().invoke(event);
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, PlayerStatePacket packet) {
            if (packet.getAction() == PlayerContext.PlayerStateAction.DELETE) {
                RemovePlayerStateEvent event = new RemovePlayerStateEvent(packet.getId());
                ClientRegistries.getEventPool().invoke(event);
                return null;
            }

            PlayerStateChangeEvent event = new PlayerStateChangeEvent(packet.getState());
            event.setSide(Side.SERVER);

            ClientRegistries.getEventPool().invoke(event);

            return null;
        }
    }

    public static class Wrapper extends PacketWrapper<PlayerStatePacket> {

        public Wrapper(MessageContext context, PlayerStatePacket packet) {
            super(context, packet);
        }
    }
}
