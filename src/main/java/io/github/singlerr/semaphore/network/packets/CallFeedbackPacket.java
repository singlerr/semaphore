/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.events.CallFeedbackEvent;
import io.github.singlerr.semaphore.events.OpponentCallAcceptedEvent;
import io.github.singlerr.semaphore.events.OpponentCallDeniedEvent;
import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.network.wrapper.PacketWrapper;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class CallFeedbackPacket extends Packet {

    private PlayerContext.CallFeedback callFeedback;

    private UUID caller;

    private UUID callee;

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        callFeedback = buffer.readEnumValue(PlayerContext.CallFeedback.class);
        caller = buffer.readUniqueId();
        callee = buffer.readUniqueId();
    }

    @Override
    public void toBytes(ByteBuf buf) {

        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeEnumValue(callFeedback);
        buffer.writeUniqueId(caller);
        buffer.writeUniqueId(callee);
    }

    public static class Handler extends PacketHandler<CallFeedbackPacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallFeedbackPacket packet) {
            CallFeedbackEvent event =
                    new CallFeedbackEvent(packet.getCaller(), packet.getCallee(), packet.getCallFeedback());
            event.setMessageContext(ctx);

            ServerRegistries.getEventPool().invoke(event);
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallFeedbackPacket packet) {

            if (Objects.requireNonNull(packet.getCallFeedback()) == PlayerContext.CallFeedback.ACCEPT) {
                ClientRegistries.getEventPool()
                        .invoke(new OpponentCallAcceptedEvent(packet.getCaller(), packet.getCallee()));
            } else {
                ClientRegistries.getEventPool()
                        .invoke(new OpponentCallDeniedEvent(
                                packet.getCaller(), packet.getCallee(), packet.getCallFeedback()));
            }

            return null;
        }
    }

    public static class Wrapper extends PacketWrapper<CallFeedbackPacket> {

        public Wrapper(MessageContext context, CallFeedbackPacket packet) {
            super(context, packet);
        }
    }
}
