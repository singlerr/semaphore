/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.compat;

import de.maxhenkel.voicechat.voice.client.AudioChannel;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AudioChannel.class)
public abstract class AudioChannelMixin {

    @ModifyArg(
            method = "writeToSpeaker",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lde/maxhenkel/voicechat/voice/client/speaker/Speaker;play([SFLjava/lang/String;)V",
                            ordinal = 0),
            index = 1,
            remap = false)
    private float semaphore$applyVolume(float volume) {

        PlayerContext context = ClientRegistries.getPlayerState();

        if (context.getCallState() != PlayerContext.CallState.IN_CALL) return volume;

        UUID opponent = context.getOpponent();
        return context.getVolumes().getOrDefault(opponent, volume);
    }
}
