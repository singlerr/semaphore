/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.sound;

import io.github.singlerr.semaphore.config.ModConfig;
import java.util.function.Supplier;
import lombok.Getter;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public final class StaticSoundRecord extends PositionedSoundRecord {

    @Getter
    private final Supplier<Double> volumeSupplier;

    public StaticSoundRecord(SoundEvent soundEvent, Supplier<Double> volumeSupplier, boolean repeat) {
        super(
                soundEvent.getSoundName(),
                SoundCategory.MASTER,
                1.0F,
                volumeSupplier.get().floatValue(),
                repeat,
                ModConfig.bellRingDelay,
                ISound.AttenuationType.NONE,
                0.0F,
                0.0F,
                0.0F);

        this.volumeSupplier = volumeSupplier;
    }
}
