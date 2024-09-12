/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.github.singlerr.semaphore.interactors.admin.presenter.data.EntityType;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public final class PacketPresentableEntities implements IMessage {

    private List<PresentableEntity> entities;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.entities = new ArrayList<>();
        int size = byteBuf.readInt();
        for (int i = 0; i < size; i++) {
            UUID id = SerializationUtils.readUUID(byteBuf);
            int state = byteBuf.readInt();
            Map<UUID, Integer> missCallCount = SerializationUtils.readMap(byteBuf, buf -> {
                UUID key = SerializationUtils.readUUID(buf);
                int val = buf.readInt();
                return new AbstractMap.SimpleImmutableEntry<>(key, val);
            });
            EntityType entityType = SerializationUtils.readEnum(EntityType.class, byteBuf);
            this.entities.add(new PresentableEntity(
                    id,
                    new PresentableEntity.State(
                            state,
                            missCallCount,
                            io.github.singlerr.semaphore.interactors.access.database.EntityType.valueOf(
                                    entityType.name()))));
        }
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeInt(this.entities.size());

        for (PresentableEntity entity : entities) {
            SerializationUtils.writeUUID(byteBuf, entity.getId());
            byteBuf.writeInt(entity.getState().getStateId());
            SerializationUtils.writeMap(entity.getState().getMissCallCount(), byteBuf, (entry, buf) -> {
                SerializationUtils.writeUUID(buf, entry.getKey());
                buf.writeInt(entry.getValue());
            });
            SerializationUtils.writeEnum(
                    EntityType.valueOf(entity.getState().getEntityType().name()), byteBuf);
        }
    }
}
