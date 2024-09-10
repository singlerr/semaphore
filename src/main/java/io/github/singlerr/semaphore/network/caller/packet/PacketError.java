/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.packet;

import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class PacketError implements IMessage {

    private UUID callerId;
    private UUID calleeId;
    private String message;

    public PacketError() {}

    public PacketError(UUID callerId, UUID calleeId, String message) {
        this.callerId = callerId;
        this.calleeId = calleeId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public UUID getCalleeId() {
        return calleeId;
    }

    public UUID getCallerId() {
        return callerId;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.callerId = SerializationUtils.readUUID(byteBuf);
        this.calleeId = SerializationUtils.readUUID(byteBuf);
        this.message = ByteBufUtils.readUTF8String(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        SerializationUtils.writeUUID(byteBuf, this.callerId);
        SerializationUtils.writeUUID(byteBuf, this.calleeId);
        ByteBufUtils.writeUTF8String(byteBuf, this.message);
    }
}
