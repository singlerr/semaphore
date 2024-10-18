/* (C) 2024 singlerr */
package io.github.singlerr.semaphore;

import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

@Config(modid = Semaphore.MOD_ID)
public final class ModConfig {

    @Config.Ignore
    public static Map<String, Double> volumes = new HashMap<>();

    @Config.Name("phone_box_sound_radius")
    @Config.Comment("Sound range that any player can hear it")
    public static float phoneBoxRadius = 5.0f;
}
