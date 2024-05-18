/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.compat;

import de.maxhenkel.voicechat.voice.client.AudioChannel;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
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
        if (Minecraft.getMinecraft().player == null) return volume;
        UUID playerId = Minecraft.getMinecraft().player.getUniqueID();
        Optional<PlayerContext> ctx = CommonRegistries.getStatePool().get(playerId, PlayerContext.class);

        if (!ctx.isPresent()) return volume;

        PlayerContext context = ctx.get();

        if (context.getCallState() != PlayerContext.CallState.IN_CALL || context.getOpponent() == PlayerContext.NULL)
            return volume;

        UUID opponent = context.getOpponent();
        return context.getVolumes().getOrDefault(opponent, volume);
    }
}
