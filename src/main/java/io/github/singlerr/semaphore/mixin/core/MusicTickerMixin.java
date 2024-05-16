/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.core;

import io.github.singlerr.semaphore.sound.ClientSoundHandler;
import io.github.singlerr.semaphore.sound.SoundWrapper;
import io.github.singlerr.semaphore.sound.VanillaAudioPlayer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTicker.class)
public abstract class MusicTickerMixin implements VanillaAudioPlayer {

    @Shadow
    @Final
    private Minecraft mc;

    @Unique
    @Nullable
    private ISound soundPlaying;

    @Unique
    private SoundEvent soundEventPlaying;

    @Unique
    private boolean delayed = false;

    @Override
    public SoundWrapper getPlaying() {
        return null;
    }

    @Override
    public void startPlaying(SoundEvent soundEvent, float volume, float pitch, boolean repeat) {
        soundPlaying = ClientSoundHandler.getRepeatable(soundEvent, repeat);
        soundEventPlaying = soundEvent;
        mc.getSoundHandler().playSound(soundPlaying);
    }

    @Override
    public void stopPlaying() {
        if (soundPlaying != null) {
            mc.getSoundHandler().stopSound(soundPlaying);
            soundPlaying = null;
        }
    }

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void semaphore$playCustomSound(CallbackInfo ci) {
        if (soundPlaying != null) {
            if (delayed && mc.getSoundHandler().isSoundPlaying(soundPlaying)) {
                delayed = false;
            }
            if (!mc.getSoundHandler().isSoundPlaying(soundPlaying) && soundPlaying.canRepeat() && !delayed) {
                soundPlaying = ClientSoundHandler.getRepeatable(soundEventPlaying, soundPlaying.canRepeat());
                mc.getSoundHandler().playDelayedSound(soundPlaying, soundPlaying.getRepeatDelay());
                delayed = true;
            } else {
                soundPlaying = null;
            }
        }
    }
}
