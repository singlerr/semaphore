/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.utils;

import io.netty.buffer.ByteBuf;
import java.util.UUID;

public final class SerializationUtils {

    private SerializationUtils() {}

    public static void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf buf) {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        return new UUID(msb, lsb);
    }

    public static <T extends Enum<T>> T readEnum(Class<T> enumClass, ByteBuf buf) {
        return enumClass.getEnumConstants()[buf.readInt()];
    }

    public static <T extends Enum<T>> void writeEnum(Enum<T> enumValue, ByteBuf buf) {
        buf.writeInt(enumValue.ordinal());
    }
}
