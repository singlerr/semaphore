/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.utils;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class SerializationUtils {

    private SerializationUtils() {
    }

    public static void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf buf) {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        return new UUID(msb, lsb);
    }

    public static <K, V> void writeMap(
            Map<K, V> map, ByteBuf buf, BiConsumer<Map.Entry<K, V>, ByteBuf> entrySerializer) {
        Set<Map.Entry<K, V>> entries = map.entrySet();
        buf.writeInt(entries.size());
        for (Map.Entry<K, V> entry : entries) {
            entrySerializer.accept(entry, buf);
        }
    }

    public static <K, V> Map<K, V> readMap(ByteBuf buf, Function<ByteBuf, Map.Entry<K, V>> entryDeserializer) {
        int size = buf.readInt();
        Map<K, V> map = new HashMap<>();

        for (int i = 0; i < size; i++) {
            Map.Entry<K, V> entry = entryDeserializer.apply(buf);
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    public static <T extends Enum<T>> T readEnum(Class<T> enumClass, ByteBuf buf) {
        return enumClass.getEnumConstants()[buf.readInt()];
    }

    public static <T extends Enum<T>> void writeEnum(Enum<T> enumValue, ByteBuf buf) {
        buf.writeInt(enumValue.ordinal());
    }
}
