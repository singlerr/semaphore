/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.core.sound;

import java.util.Map;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {

    @Accessor("invPlayingSounds")
    Map<ISound, String> getInvPlayingSounds();

    @Accessor("playingSounds")
    Map<String, ISound> getPlayingSounds();
}
