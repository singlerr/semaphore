/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.sound;

import net.minecraft.client.audio.PositionedSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PositionedSound.class)
public interface PositionedSoundAccessor {

    @Accessor("repeat")
    void setRepeat(boolean v);

    @Accessor("volume")
    void setVolume(float v);
}
