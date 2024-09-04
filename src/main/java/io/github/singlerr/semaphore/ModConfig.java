/* (C) 2024 singlerr */
package io.github.singlerr.semaphore;

import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.common.config.Config;

@Config(modid = Semaphore.MOD_ID)
public final class ModConfig {

    @Config.Name("volumes")
    @Config.Comment(
            "Set of player unique id - volume entries. Volume is range of 0.0 ~ 1.0, which 1.0 means full volume and 0.0 means mute.")
    public static Map<String, Double> volumes = new HashMap<>();
}
