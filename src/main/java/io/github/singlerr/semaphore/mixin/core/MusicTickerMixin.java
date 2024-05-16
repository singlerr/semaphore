/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.core;

import io.github.singlerr.semaphore.sound.ClientSoundHandler;
import io.github.singlerr.semaphore.sound.VanillaAudioPlayer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MusicTicker.class)
public abstract class MusicTickerMixin implements VanillaAudioPlayer {

    @Shadow
    @Final
    private Minecraft mc;

    @Unique
    private Map<ResourceLocation, ISound> sounds = new HashMap<>();

    @Override
    public void play(SoundEvent soundEvent, boolean repeat) {
        ISound sound;
        ResourceLocation soundName = soundEvent.getSoundName();
        if (sounds.containsKey(soundName)) {
            sound = sounds.get(soundName);
        } else {
            sound = ClientSoundHandler.getRepeatable(soundEvent, repeat);
            sounds.put(soundName, sound);
        }

        if (mc.getSoundHandler().isSoundPlaying(sound)) {
            stop(soundEvent);
        }

        mc.getSoundHandler().playSound(sound);
    }

    @Override
    public void stop(SoundEvent soundEvent) {
        if (sounds.containsKey(soundEvent.getSoundName())) {
            String key = getKey(sounds.get(soundEvent.getSoundName()));
            mc.getSoundHandler().stop(key, SoundCategory.MASTER);
        }
    }

    @Unique
    private String getKey(ISound sound) {
        SoundHandlerAccessor soundHandler = (SoundHandlerAccessor) mc.getSoundHandler();
        SoundManagerAccessor accessor = (SoundManagerAccessor) soundHandler.getSoundManager();

        Map<ISound, String> map = accessor.getInvPlayingSounds();

        return map.get(sound);
    }
}
