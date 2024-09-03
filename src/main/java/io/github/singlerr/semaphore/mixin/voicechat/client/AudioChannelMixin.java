/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.voicechat.client;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.voice.client.AudioChannel;
import io.github.singlerr.semaphore.Constants;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import io.github.singlerr.semaphore.utils.RadioFilter;
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
        return volume;
    }

    @ModifyArg(
            method = "writeToSpeaker",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lde/maxhenkel/voicechat/voice/client/speaker/Speaker;play([SFLjava/lang/String;)V",
                            ordinal = 0),
            index = 0,
            remap = false)
    private short[] semaphore$applyBandpassFilter(short[] data) {
        if (ClientResources.StaticResources.VOICECHAT_CLIENT_API == null) return data;

        Group group = ClientResources.StaticResources.VOICECHAT_CLIENT_API.getGroup();

        if (group == null) return data;

        if (!group.getName().equals(Constants.P2P_GROUP)) return data;

        return RadioFilter.getInstance().apply(data);
    }
}
