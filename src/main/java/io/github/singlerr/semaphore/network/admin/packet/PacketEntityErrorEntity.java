/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class PacketEntityErrorEntity implements IMessage {

    private String message;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.message = ByteBufUtils.readUTF8String(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        ByteBufUtils.writeUTF8String(byteBuf, this.message);
    }
}
