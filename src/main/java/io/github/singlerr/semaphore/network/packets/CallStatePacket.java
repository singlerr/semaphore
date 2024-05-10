/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.network.wrapper.PacketWrapper;
import io.github.singlerr.semaphore.regisries.CommonRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@SuperBuilder
@NoArgsConstructor
@Getter
public final class CallStatePacket extends Packet {

    private PlayerContext.CallState action;

    private UUID caller;

    private UUID callee;

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeEnumValue(action);
        buffer.writeUniqueId(caller);
        buffer.writeUniqueId(callee);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        action = buffer.readEnumValue(PlayerContext.CallState.class);
        caller = buffer.readUniqueId();
        callee = buffer.readUniqueId();
    }

    public static class Handler extends PacketHandler<CallStatePacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallStatePacket packet) {

            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallStatePacket packet) {
            CommonRegistries.getEventPool().invoke(packet);
            CommonRegistries.getEventPool().invoke(new CallStatePacket.Wrapper(ctx, packet));
            return null;
        }
    }

    public static class Wrapper extends PacketWrapper<CallStatePacket> {

        public Wrapper(MessageContext context, CallStatePacket packet) {
            super(context, packet);
        }
    }
}
