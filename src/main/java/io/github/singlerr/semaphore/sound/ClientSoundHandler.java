/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.sound;

import io.github.singlerr.semaphore.config.ModConfig;
import io.github.singlerr.semaphore.mixin.core.sound.SoundHandlerAccessor;
import io.github.singlerr.semaphore.mixin.core.sound.SoundManagerAccessor;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.sound.utils.AudioPlayer;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Log4j2
@SideOnly(Side.CLIENT)
@UtilityClass
public class ClientSoundHandler {

    private final Map<ResourceLocation, ISound> sounds = new HashMap<>();

    public static void playCallEstablishedSound() {
        playNonRepeatable(ClientRegistries.SOUND_CALL_YES, () -> ModConfig.soundSettings.callAcceptVolume);
    }

    public static void playRejectedBy(PlayerContext.CallRejectReason reason) {
        if (reason == PlayerContext.CallRejectReason.PLAYER_IN_CALL) {
            playNonRepeatable(ClientRegistries.IN_CALL_SOUND, ModConfig.soundSettings.playerInCallVolume);
        } else {
            playNonRepeatable(ClientRegistries.MISS_CALL_SOUND, ModConfig.soundSettings.missCallVolume);
        }
        playNonRepeatable(ClientRegistries.SOUND_CALL_NO, () -> ModConfig.soundSettings.callDenyVolume);
    }

    public static void playTouchSound() {
        playNonRepeatable(ClientRegistries.SOUND_PHONE_TOUCH, () -> ModConfig.soundSettings.touchVolume);
    }

    public static void playOkSound() {
        playNonRepeatable(ClientRegistries.SOUND_CALL_YES, () -> ModConfig.soundSettings.callAcceptVolume);
    }

    public static void playNoSound() {
        playNonRepeatable(ClientRegistries.SOUND_CALL_NO, () -> ModConfig.soundSettings.callDenyVolume);
    }

    public static void playCallClosedSound() {
        playNonRepeatable(ClientRegistries.SOUND_CALL_OFF, () -> ModConfig.soundSettings.callCloseVolume);
    }

    public static void stopCallingSound() {
        stopRepeatable(ClientRegistries.SOUND_CALLING);
    }

    public static void stopReceivingCallSound() {
        stopRepeatable(ModConfig.bellRing ? ClientRegistries.SOUND_PHONE_BELL : ClientRegistries.SOUND_PHONE_VIBRATE);
    }

    public static void playCallingSound() {
        playRepeatable(ClientRegistries.SOUND_CALLING, () -> ModConfig.soundSettings.callingVolume);
    }

    public static void playReceivingCallSound() {
        if (ModConfig.bellRing) {
            playRepeatable(ClientRegistries.SOUND_PHONE_BELL, () -> ModConfig.soundSettings.ringVolume);
        } else {
            playRepeatable(ClientRegistries.SOUND_PHONE_VIBRATE, () -> ModConfig.soundSettings.vibrateVolume);
        }
    }

    private void playNonRepeatable(ResourceLocationBuilder resourceLocationBuilder, double volume) {
        try {
            AudioPlayer.play(resourceLocationBuilder, () -> 100 * volume);
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    private void playNonRepeatable(SoundEvent soundEvent, Supplier<Double> volumeSupplier) {
        Minecraft.getMinecraft().getSoundHandler().playSound(getRepeatable(soundEvent, volumeSupplier, false));
    }

    private void playRepeatable(SoundEvent soundEvent, Supplier<Double> volumeSupplier) {
        ISound sound;

        if (sounds.containsKey(soundEvent.getSoundName())) {
            sound = sounds.get(soundEvent.getSoundName());
        } else {
            sound = getRepeatable(soundEvent, volumeSupplier, true);
            sounds.put(soundEvent.getSoundName(), sound);
        }
        stopRepeatable(soundEvent);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }

    private void stopRepeatable(SoundEvent soundEvent) {
        if (sounds.containsKey(soundEvent.getSoundName())) {
            ISound sound = sounds.get(soundEvent.getSoundName());
            Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
            removeSound(sound);
        }
    }

    private void stopAll() {
        for (ISound value : sounds.values()) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(value);
        }
        removeAll();
    }

    private String getSoundKey(ISound sound) {
        SoundHandlerAccessor soundHandler =
                (SoundHandlerAccessor) Minecraft.getMinecraft().getSoundHandler();
        SoundManagerAccessor accessor = (SoundManagerAccessor) soundHandler.getSoundManager();

        Map<ISound, String> map = accessor.getInvPlayingSounds();

        return map.get(sound);
    }

    private void removeSound(ISound sound) {
        SoundHandlerAccessor soundHandler =
                (SoundHandlerAccessor) Minecraft.getMinecraft().getSoundHandler();
        SoundManagerAccessor accessor = (SoundManagerAccessor) soundHandler.getSoundManager();

        Map<ISound, String> map = accessor.getInvPlayingSounds();

        String key = map.remove(sound);
        accessor.getPlayingSounds().remove(key);
    }

    private void removeAll() {
        SoundHandlerAccessor soundHandler =
                (SoundHandlerAccessor) Minecraft.getMinecraft().getSoundHandler();
        SoundManagerAccessor accessor = (SoundManagerAccessor) soundHandler.getSoundManager();

        Map<ISound, String> map = accessor.getInvPlayingSounds();
        map.clear();
        accessor.getPlayingSounds().clear();
    }

    public StaticSoundRecord getRepeatable(SoundEvent soundEvent, Supplier<Double> volumeSupplier, boolean repeat) {
        return new StaticSoundRecord(soundEvent, volumeSupplier, repeat);
    }
}
