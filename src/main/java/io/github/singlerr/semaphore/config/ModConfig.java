/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.config;

import io.github.singlerr.semaphore.Semaphore;
import net.minecraftforge.common.config.Config;

@Config(modid = Semaphore.MOD_ID)
public final class ModConfig {

    @Config.Comment({"Set timeout of calling phones", "unit: seconds"})
    @Config.Name("callTimeout")
    @Config.RangeDouble(min = 1, max = Double.MAX_VALUE)
    public static double callTimeout = 40;

    @Config.Name("bellRing")
    public static boolean bellRing = true;

    @Config.Name("bellRingDelay")
    @Config.Comment({"Set delay of bell ring", "unit: milliseconds"})
    public static int bellRingDelay = 10;
}
