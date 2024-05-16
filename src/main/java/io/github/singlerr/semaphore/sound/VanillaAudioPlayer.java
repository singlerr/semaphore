/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.sound;

import net.minecraft.util.SoundEvent;

public interface VanillaAudioPlayer {

    SoundWrapper getPlaying();

    void startPlaying(SoundEvent soundEvent, float volume, float pitch, boolean repeat);

    void stopPlaying();
}
