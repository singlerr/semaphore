package io.github.singlerr.semaphore.mixin.compat;

import de.maxhenkel.voicechat.voice.client.ClientVoicechat;
import de.maxhenkel.voicechat.voice.client.speaker.SpeakerException;
import de.maxhenkel.voicechat.voice.client.speaker.SpeakerManager;
import io.github.singlerr.semaphore.sound.utils.AudioPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientVoicechat.class)
public abstract class ClientVoicechatMixin {

    @Inject(method = "reloadAudio", at = @At("TAIL"), remap = false)
    private void semaphore$reloadSpeaker(CallbackInfo ci){
        try {
            AudioPlayer.setSpeaker(SpeakerManager.createSpeaker(null, UUID.randomUUID()));
        } catch (SpeakerException e) {
            throw new RuntimeException(e);
        }
    }
}
