/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class PacketPresentableCallConnection implements IMessage {

    private UUID id;
    private UUID callerId;
    private UUID calleeId;
    private boolean alive;

    public PacketPresentableCallConnection() {}

    public PacketPresentableCallConnection(UUID id, UUID callerId, UUID calleeId, boolean alive) {
        this.id = id;
        this.callerId = callerId;
        this.calleeId = calleeId;
        this.alive = alive;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCallerId() {
        return callerId;
    }

    public UUID getCalleeId() {
        return calleeId;
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.id = SerializationUtils.readUUID(byteBuf);
        this.callerId = SerializationUtils.readUUID(byteBuf);
        this.calleeId = SerializationUtils.readUUID(byteBuf);
        this.alive = byteBuf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        SerializationUtils.writeUUID(byteBuf, this.id);
        SerializationUtils.writeUUID(byteBuf, this.callerId);
        SerializationUtils.writeUUID(byteBuf, this.calleeId);
        byteBuf.writeBoolean(this.alive);
    }
}
