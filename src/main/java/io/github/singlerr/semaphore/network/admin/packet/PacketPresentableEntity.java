/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.github.singlerr.semaphore.interactors.admin.presenter.data.EntityType;
import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public final class PacketPresentableEntity implements IMessage {

    private UUID id;
    private int stateId;
    private Map<UUID, Integer> missCallCount;
    private EntityType entityType;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.id = SerializationUtils.readUUID(byteBuf);
        this.stateId = byteBuf.readInt();
        this.missCallCount = SerializationUtils.readMap(byteBuf, buf -> {
            UUID key = SerializationUtils.readUUID(buf);
            int val = buf.readInt();
            return new AbstractMap.SimpleImmutableEntry<>(key, val);
        });
        this.entityType = SerializationUtils.readEnum(EntityType.class, byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        SerializationUtils.writeUUID(byteBuf, this.id);
        byteBuf.writeInt(this.stateId);
        SerializationUtils.writeMap(this.missCallCount, byteBuf, (entry, buf) -> {
            SerializationUtils.writeUUID(buf, entry.getKey());
            buf.writeInt(entry.getValue());
        });
        SerializationUtils.writeEnum(this.entityType, byteBuf);
    }
}
