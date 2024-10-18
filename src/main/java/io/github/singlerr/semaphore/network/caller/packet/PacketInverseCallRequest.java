/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.packet;

import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public final class PacketInverseCallRequest implements IMessage {

    private UUID callerId;
    private UUID calleeId;

    public PacketInverseCallRequest() {
    }

    public PacketInverseCallRequest(UUID callerId, UUID calleeId) {
        this.callerId = callerId;
        this.calleeId = calleeId;
    }

    public UUID getCallerId() {
        return callerId;
    }

    public UUID getCalleeId() {
        return calleeId;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.callerId = SerializationUtils.readUUID(byteBuf);
        this.calleeId = SerializationUtils.readUUID(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        SerializationUtils.writeUUID(byteBuf, this.callerId);
        SerializationUtils.writeUUID(byteBuf, this.calleeId);
    }
}
