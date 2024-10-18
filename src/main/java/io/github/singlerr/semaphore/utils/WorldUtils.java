/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

@UtilityClass
public class WorldUtils {

    public <T extends Entity> Collection<T> getEntitiesNearby(
            Class<T> entityType, World world, BlockPos pos, double distance) {
        return world.getEntities(entityType, e -> filterByDistance(e, pos, distance));
    }

    private double getDistance(BlockPos a, BlockPos b) {
        return a.getDistance(b.getX(), b.getY(), b.getZ());
    }

    private boolean filterByDistance(Entity e, BlockPos pos, double distance) {
        return getDistance(e.getPosition(), pos) <= distance;
    }
}
