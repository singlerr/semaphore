/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import java.util.UUID;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.BlockPos;

@UtilityClass
public class Utils {
    public UUID packToUUID(BlockPos pos) {
        long msb = ((long) pos.getX()) << 32 | pos.getY();
        long lsb = pos.getZ();
        return new UUID(msb, lsb);
    }

    public BlockPos fromUUID(UUID uuid) {
        int x = (int) (uuid.getMostSignificantBits() >> 32);
        int y = (int) uuid.getMostSignificantBits();
        int z = (int) uuid.getLeastSignificantBits();
        return new BlockPos(x, y, z);
    }
}
