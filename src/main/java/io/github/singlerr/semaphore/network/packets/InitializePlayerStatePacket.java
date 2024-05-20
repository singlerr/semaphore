/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.packets;

import io.github.singlerr.semaphore.network.Packet;
import io.github.singlerr.semaphore.network.PacketHandler;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Log4j2
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitializePlayerStatePacket extends Packet {

    private List<PlayerContext> contexts;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        PacketBuffer wrapper = new PacketBuffer(byteBuf);
        int size = wrapper.readInt();
        contexts = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            PlayerContext ctx = PlayerContext.from(wrapper);
            contexts.add(ctx);
        }
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        PacketBuffer wrapper = new PacketBuffer(byteBuf);
        wrapper.writeInt(contexts.size());
        for (int i = 0; i < contexts.size(); i++) {
            contexts.get(i).serialize(wrapper);
        }
    }

    public static class Handler extends PacketHandler<InitializePlayerStatePacket> {

        @Override
        protected Packet handleC2S(MessageContext ctx, InitializePlayerStatePacket packet) {
            return null;
        }

        @Override
        protected Packet handleS2C(MessageContext ctx, InitializePlayerStatePacket packet) {
            List<PlayerContext> contexts = packet.getContexts();
            ClientRegistries.getPhoneScreen().clearPlayerStates(contexts);
            return null;
        }
    }
}
