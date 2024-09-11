/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.sounds;

public interface SoundPlayer {

    SoundKey playSound(SoundResource sound, float pitch, float volume, boolean repeat, boolean stopPrevious);

    void stopSound(SoundKey soundKey);

    void stopSound(SoundResource sound);
}
