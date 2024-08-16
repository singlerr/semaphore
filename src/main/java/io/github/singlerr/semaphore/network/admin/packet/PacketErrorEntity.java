/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class PacketErrorEntity implements IMessage {

    private String message;

    public PacketErrorEntity() {}

    public String getMessage() {
        return message;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.message = ByteBufUtils.readUTF8String(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        ByteBufUtils.writeUTF8String(byteBuf, this.message);
    }
}
