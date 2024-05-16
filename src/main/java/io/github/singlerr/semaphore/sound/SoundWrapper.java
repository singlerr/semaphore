/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.sound;

import lombok.Data;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.SoundEvent;

@Data(staticConstructor = "of")
public class SoundWrapper {

    private final SoundEvent key;

    private final ISound soundObject;
}
