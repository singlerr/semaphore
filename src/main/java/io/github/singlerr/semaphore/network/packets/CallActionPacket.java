/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.events.CallClosedEvent;
import io.github.singlerr.semaphore.events.CallEvent;
import io.github.singlerr.semaphore.events.ReceivingCallEvent;
import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.ClientUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class CallActionPacket extends Packet {

    private PlayerContext.CallAction action;

    private UUID caller;

    private UUID callee;

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        action = buffer.readEnumValue(PlayerContext.CallAction.class);
        caller = buffer.readUniqueId();
        callee = buffer.readUniqueId();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeEnumValue(action);
        buffer.writeUniqueId(caller);
        buffer.writeUniqueId(callee);
    }

    public static class Handler extends PacketHandler<CallActionPacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, CallActionPacket packet) {
            CallEvent event = new CallEvent(packet.getCallee(), packet.getCaller(), packet.getAction());
            event.setMessageContext(ctx);
            ServerRegistries.getEventPool().invoke(event);
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, CallActionPacket packet) {
            UUID playerId = ClientUtils.getClientUniqueId();
            if (packet.getAction() == PlayerContext.CallAction.REQUEST) {
                if (packet.getCallee().equals(playerId)) {
                    ClientRegistries.getEventPool()
                            .invoke(new ReceivingCallEvent(packet.getCallee(), packet.getCaller()));
                }
            } else if (packet.getAction() == PlayerContext.CallAction.CLOSE) {
                if (packet.getCallee().equals(playerId) || packet.getCaller().equals(playerId)) {
                    ClientRegistries.getEventPool().invoke(new CallClosedEvent(packet.getCallee(), packet.getCaller()));
                }
            }
            return null;
        }
    }
}
