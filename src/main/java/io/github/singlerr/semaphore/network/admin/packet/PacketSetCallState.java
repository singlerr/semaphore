/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class PacketSetCallState implements IMessage {

    private UUID id;
    private int stateId;
    private int missCallCount;

    public PacketSetCallState() {}

    public PacketSetCallState(UUID id, int stateId, int missCallCount) {
        this.id = id;
        this.stateId = stateId;
        this.missCallCount = missCallCount;
    }

    public UUID getId() {
        return id;
    }

    public int getStateId() {
        return stateId;
    }

    public int getMissCallCount() {
        return missCallCount;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.id = SerializationUtils.readUUID(byteBuf);
        this.stateId = byteBuf.readInt();
        this.missCallCount = byteBuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        SerializationUtils.writeUUID(byteBuf, this.id);
        byteBuf.writeInt(this.stateId);
        byteBuf.writeInt(this.missCallCount);
    }
}
