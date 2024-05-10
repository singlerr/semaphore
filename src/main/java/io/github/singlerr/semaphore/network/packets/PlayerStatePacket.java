/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.network.wrapper.PacketWrapper;
import io.github.singlerr.semaphore.regisries.CommonRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@SuperBuilder
@NoArgsConstructor
@Getter
public final class PlayerStatePacket extends Packet {

    private PlayerContext state;

    private UUID id;

    @Override
    public void fromBytes(ByteBuf buf) {
        id = SerializationUtils.readUUID(buf);
        state = PlayerContext.builder().build();
        state.deserialize(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        SerializationUtils.writeUUID(buf, id);
        state.serialize(buf);
    }

    @NoArgsConstructor
    public static class Handler extends PacketHandler<PlayerStatePacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, PlayerStatePacket packet) {
            CommonRegistries.getEventPool().invoke(packet);
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, PlayerStatePacket packet) {
            CommonRegistries.getEventPool().invoke(packet);
            CommonRegistries.getEventPool().invoke(new PlayerStatePacket.Wrapper(ctx, packet));
            return null;
        }
    }

    public static class Wrapper extends PacketWrapper<PlayerStatePacket> {

        public Wrapper(MessageContext context, PlayerStatePacket packet) {
            super(context, packet);
        }
    }
}
