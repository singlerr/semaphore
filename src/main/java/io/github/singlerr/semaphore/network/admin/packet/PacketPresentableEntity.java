/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class PacketPresentableEntity implements IMessage {

    private UUID id;
    private int state;

    public PacketPresentableEntity() {}

    public PacketPresentableEntity(UUID id, int state) {
        this.id = id;
        this.state = state;
    }

    public UUID getId() {
        return id;
    }

    public int getState() {
        return state;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.id = SerializationUtils.readUUID(byteBuf);
        this.state = byteBuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        SerializationUtils.writeUUID(byteBuf, this.id);
        byteBuf.writeInt(this.state);
    }
}
