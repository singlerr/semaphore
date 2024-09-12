/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@NoArgsConstructor
@Getter
public final class PacketGetAllEntities implements IMessage {

    @Override
    public void fromBytes(ByteBuf byteBuf) {}

    @Override
    public void toBytes(ByteBuf byteBuf) {}
}
