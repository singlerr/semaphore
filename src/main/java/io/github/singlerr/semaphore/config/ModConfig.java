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

    public static InteractionSoundSettings soundSettings = new InteractionSoundSettings();

    public static class InteractionSoundSettings {

        @Config.Name("touchVolume")
        @Config.RangeDouble(min = 0, max = 1)
        public double touchVolume = 0.5;

        @Config.Name("ringVolume")
        @Config.RangeDouble(min = 0, max = 1)
        public double ringVolume = 0.5;

        @Config.Name("vibrateVolume")
        @Config.RangeDouble(min = 0, max = 1)
        public double vibrateVolume = 0.5;

        @Config.Name("callVolume")
        @Config.RangeDouble(min = 0, max = 1)
        public double callingVolume = 0.5;

        @Config.Name("callAcceptVolume")
        @Config.RangeDouble(min = 0, max = 1)
        public double callAcceptVolume = 0.5;

        @Config.Name("callRejectVolume")
        @Config.RangeDouble(min = 0, max = 1)
        public double callDenyVolume = 0.5;

        @Config.Name("callCloseVolume")
        @Config.RangeDouble(min = 0, max = 1)
        public double callCloseVolume = 0.5;

        @Config.Name("missCallVolume")
        @Config.RangeDouble(min = 0, max = 1)
        public double missCallVolume = 0.5;

        @Config.Name("playerInCallVolume")
        @Config.RangeDouble(min = 0, max = 1)
        public double playerInCallVolume = 0.5;
    }
}
