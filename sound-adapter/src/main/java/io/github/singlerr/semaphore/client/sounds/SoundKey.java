/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.sounds;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class SoundKey {

    private final UUID id;
    private final SoundResource sound;
    private final Consumer<Float> volumeSetter;

    public SoundKey(UUID id, SoundResource sound, Consumer<Float> volumeSetter) {
        this.id = id;
        this.sound = sound;
        this.volumeSetter = volumeSetter;
    }

    public UUID getId() {
        return id;
    }

    public SoundResource getSound() {
        return sound;
    }

    public Consumer<Float> getVolumeSetter() {
        return volumeSetter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoundKey soundKey = (SoundKey) o;
        return Objects.equals(id, soundKey.id) && sound == soundKey.sound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sound);
    }
}
