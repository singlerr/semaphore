/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.events;

import lombok.Data;
import net.minecraft.util.SoundEvent;

@Data
public final class PlaySoundCommand {

    private final SoundEvent sound;

    private final boolean repeat;
}
