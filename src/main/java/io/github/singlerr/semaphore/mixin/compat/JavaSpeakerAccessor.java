/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.compat;

import de.maxhenkel.voicechat.voice.client.speaker.JavaSpeakerBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JavaSpeakerBase.class)
public interface JavaSpeakerAccessor {

    @Invoker("getAvailableSamples")
    int invokeGetAvailableSamples();
}
