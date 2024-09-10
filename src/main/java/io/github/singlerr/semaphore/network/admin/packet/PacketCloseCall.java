/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class PacketCloseCall implements IMessage {

    private UUID callerId;
    private UUID calleeId;

    public PacketCloseCall() {}

    public PacketCloseCall(UUID callerId, UUID calleeId) {
        this.callerId = callerId;
        this.calleeId = calleeId;
    }

    public UUID callerId() {
        return callerId;
    }

    public UUID calleeId() {
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
