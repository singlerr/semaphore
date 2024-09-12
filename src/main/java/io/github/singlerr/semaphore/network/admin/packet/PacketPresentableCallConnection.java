/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.packet;

import io.github.singlerr.semaphore.network.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class PacketPresentableCallConnection implements IMessage {

    private UUID id;
    private UUID callerId;
    private UUID calleeId;
    private boolean alive;

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
