/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class PacketPresentableEntity implements IMessage {

    private UUID id;
    private int stateId;
    private Map<UUID, Integer> missCallCount;

    public PacketPresentableEntity() {}

    public PacketPresentableEntity(UUID id, int stateId, Map<UUID, Integer> missCallCount) {
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

    public Map<UUID, Integer> getMissCallCount() {
        return missCallCount;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.id = SerializationUtils.readUUID(byteBuf);
        this.stateId = byteBuf.readInt();
        this.missCallCount = SerializationUtils.readMap(byteBuf, buf -> {
            UUID key = SerializationUtils.readUUID(buf);
            int val = buf.readInt();
            return new AbstractMap.SimpleImmutableEntry<>(key, val);
        });
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        SerializationUtils.writeUUID(byteBuf, this.id);
        byteBuf.writeInt(this.stateId);
        SerializationUtils.writeMap(this.missCallCount, byteBuf, (entry, buf) -> {
            SerializationUtils.writeUUID(buf, entry.getKey());
            buf.writeInt(entry.getValue());
        });
    }
}
