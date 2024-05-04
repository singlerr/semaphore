/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SerializationUtils {

    public UUID readUUID(ByteBuf buf) {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        return new UUID(msb, lsb);
    }

    public void writeUUID(ByteBuf buf, UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();

        buf.writeLong(msb);
        buf.writeLong(lsb);
    }
}
