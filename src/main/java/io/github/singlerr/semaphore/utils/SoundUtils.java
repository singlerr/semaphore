/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public final class SoundUtils {

    private SoundUtils() {}

    public static PositionedSound getRepeatingRecord(ResourceLocation sound, float pitch, float volume) {
        return new PositionedSoundRecord(
                sound, SoundCategory.MASTER, volume, pitch, true, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f);
    }

    public static PositionedSound getRecord(ResourceLocation sound, float pitch, float volume) {
        return new PositionedSoundRecord(
                sound, SoundCategory.MASTER, volume, pitch, false, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f);
    }

    public static PositionedSound getRecord(ResourceLocation sound, float pitch, float volume, boolean repeat) {
        return new PositionedSoundRecord(
                sound, SoundCategory.MASTER, volume, pitch, repeat, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f);
    }
}
