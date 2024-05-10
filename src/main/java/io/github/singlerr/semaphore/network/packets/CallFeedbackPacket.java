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
public final class CallFeedbackPacket extends Packet {

    private PlayerContext.CallFeedback callFeedback;

    private UUID caller;

    private UUID callee;

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeEnumValue(callFeedback);
        buffer.writeUniqueId(caller);
        buffer.writeUniqueId(caller);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        callFeedback = buffer.readEnumValue(PlayerContext.CallFeedback.class);
        caller = buffer.readUniqueId();
        callee = buffer.readUniqueId();
    }

    public static class Handler extends PacketHandler<CallFeedbackPacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallFeedbackPacket packet) {
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallFeedbackPacket packet) {
            CommonRegistries.getEventPool().invoke(packet);
            CommonRegistries.getEventPool().invoke(new Wrapper(ctx, packet));
            return null;
        }
    }

    public static class Wrapper extends PacketWrapper<CallFeedbackPacket> {

        public Wrapper(MessageContext context, CallFeedbackPacket packet) {
            super(context, packet);
        }
    }
}
