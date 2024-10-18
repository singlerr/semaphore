/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.sounds;

public final class SoundPlayerAccess {

    private static SoundPlayer instance;

    private SoundPlayerAccess() {
    }

    public static SoundPlayer getInstance() {
        return instance;
    }

    public static void setInstance(SoundPlayer instance) {
        if (SoundPlayerAccess.instance != null) throw new IllegalStateException("Cannot assign twice!");

        SoundPlayerAccess.instance = instance;
    }
}
