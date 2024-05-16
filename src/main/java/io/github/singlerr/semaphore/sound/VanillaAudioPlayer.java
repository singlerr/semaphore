/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.sound;

import net.minecraft.util.SoundEvent;

public interface VanillaAudioPlayer {

    void play(SoundEvent soundEvent, boolean repeat);

    void stop(SoundEvent soundEvent);
}
