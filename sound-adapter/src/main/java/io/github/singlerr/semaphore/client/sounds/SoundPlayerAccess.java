/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.sounds;

public final class SoundPlayerAccess {

    private SoundPlayerAccess() {}

    private static SoundPlayer instance;

    public static void setInstance(SoundPlayer instance) {
        if (SoundPlayerAccess.instance != null) throw new IllegalStateException("Cannot assign twice!");

        SoundPlayerAccess.instance = instance;
    }

    public static SoundPlayer getInstance() {
        return instance;
    }
}
