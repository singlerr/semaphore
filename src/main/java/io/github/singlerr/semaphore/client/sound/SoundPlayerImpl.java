/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.sound;

import io.github.singlerr.semaphore.client.sounds.SoundKey;
import io.github.singlerr.semaphore.client.sounds.SoundPlayer;
import io.github.singlerr.semaphore.client.sounds.SoundResource;
import io.github.singlerr.semaphore.mixin.sound.PositionedSoundAccessor;
import io.github.singlerr.semaphore.utils.SoundUtils;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SoundPlayerImpl implements SoundPlayer {

    private final Map<SoundKey, PositionedSound> playingSounds;
    private final SoundHandler soundHandler;

    public SoundPlayerImpl(SoundHandler soundHandler) {
        this.playingSounds = new ConcurrentHashMap<>();
        this.soundHandler = soundHandler;
    }

    @Override
    public SoundKey playSound(SoundResource sound, float pitch, float volume, boolean repeat, boolean stopPrevious) {
        if (repeat || stopPrevious) {
            Optional<Map.Entry<SoundKey, PositionedSound>> opt = getAlreadyPlaying(sound);

            // If there is the repeating sound already, then stop it.
            opt.ifPresent(e -> {
                synchronized (e.getValue()) {
                    ((PositionedSoundAccessor) e.getValue()).setRepeat(false);
                    soundHandler.stopSound(e.getValue());
                    playingSounds.remove(e.getKey());
                }
            });
        }
        PositionedSound s = SoundUtils.getRecord(new ResourceLocation(sound.getName()), 1.0f, 1.0f, repeat);
        SoundKey key = new SoundKey(UUID.randomUUID(), sound, (v) -> {
            ((PositionedSoundAccessor) s).setVolume(v);
        });
        playingSounds.put(key, s);
        soundHandler.playSound(s);
        return key;
    }

    @Override
    public SoundKey playSound(
            SoundResource sound, float pitch, float volume, int repeatDelay, boolean repeat, boolean stopPrevious) {
        if (repeat || stopPrevious) {
            Optional<Map.Entry<SoundKey, PositionedSound>> opt = getAlreadyPlaying(sound);

            // If there is the repeating sound already, then stop it.
            opt.ifPresent(e -> {
                synchronized (e.getValue()) {
                    ((PositionedSoundAccessor) e.getValue()).setRepeat(false);
                    soundHandler.stopSound(e.getValue());
                    playingSounds.remove(e.getKey());
                }
            });
        }
        PositionedSound s =
                SoundUtils.getRecord(new ResourceLocation(sound.getName()), 1.0f, 1.0f, repeatDelay, repeat);
        SoundKey key = new SoundKey(UUID.randomUUID(), sound, (v) -> {
            ((PositionedSoundAccessor) s).setVolume(v);
        });
        playingSounds.put(key, s);
        soundHandler.playSound(s);
        return key;
    }

    @Override
    public void stopSound(SoundKey soundKey) {
        PositionedSound sound = playingSounds.get(soundKey);
        if (sound != null) {
            synchronized (sound) {
                ((PositionedSoundAccessor) sound).setRepeat(false);
                soundHandler.stopSound(sound);
                playingSounds.remove(soundKey);
            }
        }
    }

    @Override
    public void stopSound(SoundResource sound) {
        Optional<Map.Entry<SoundKey, PositionedSound>> opt = getAlreadyPlaying(sound);
        if (opt.isPresent()) {
            SoundKey key = opt.get().getKey();
            PositionedSound s = opt.get().getValue();
            synchronized (s) {
                ((PositionedSoundAccessor) s).setRepeat(false);
                soundHandler.stopSound(s);
                playingSounds.remove(key);
            }
        }
    }

    private Optional<Map.Entry<SoundKey, PositionedSound>> getAlreadyPlaying(SoundResource sound) {
        return playingSounds.entrySet().stream()
                .filter(e -> e.getKey().getSound().equals(sound))
                .findAny();
    }
}
