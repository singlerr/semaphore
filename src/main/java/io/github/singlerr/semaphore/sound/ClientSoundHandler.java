/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.sound;

import io.github.singlerr.semaphore.config.ModConfig;
import io.github.singlerr.semaphore.events.*;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.utils.EventPool;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@UtilityClass
public class ClientSoundHandler {

    public void register(EventPool eventPool) {
        eventPool.subscribe(ReceivingCallEvent.class, ClientSoundHandler::handleRing);
        eventPool.subscribe(StopSoundCommand.class, ClientSoundHandler::stopSound);
        eventPool.subscribe(PlaySoundCommand.class, ClientSoundHandler::playSound);
        eventPool.subscribe(SendingCallEvent.class, ClientSoundHandler::handlePhoneCall);
        eventPool.subscribe(CallAcceptedEvent.class, ClientSoundHandler::handleAcceptPhoneCall);
    }

    //
    // GUI
    //

    private void handleRing(ReceivingCallEvent event) {
        VanillaAudioPlayer soundPlayer =
                (VanillaAudioPlayer) Minecraft.getMinecraft().getMusicTicker();
        soundPlayer.startPlaying(
                ModConfig.bellRing ? ClientRegistries.SOUND_PHONE_BELL : ClientRegistries.SOUND_PHONE_VIBRATE,
                1.0F,
                1.0F,
                true);
    }

    private void handlePhoneCall(SendingCallEvent event) {
        VanillaAudioPlayer soundPlayer =
                (VanillaAudioPlayer) Minecraft.getMinecraft().getMusicTicker();
        soundPlayer.startPlaying(ClientRegistries.SOUND_CALLING, 1.0F, 1.0F, true);
    }

    private void handleAcceptPhoneCall(CallAcceptedEvent event) {
        VanillaAudioPlayer soundPlayer =
                (VanillaAudioPlayer) Minecraft.getMinecraft().getMusicTicker();
        soundPlayer.stopPlaying();
    }

    private void stopSound(StopSoundCommand command) {
        VanillaAudioPlayer soundPlayer =
                (VanillaAudioPlayer) Minecraft.getMinecraft().getMusicTicker();
        soundPlayer.stopPlaying();
    }

    private void playSound(PlaySoundCommand command) {
        Minecraft.getMinecraft().getSoundHandler().playSound(getRepeatable(command.getSound(), false));
    }

    public PositionedSoundRecord getRepeatable(SoundEvent soundEvent, boolean repeat) {
        return new PositionedSoundRecord(
                soundEvent.getSoundName(),
                SoundCategory.MASTER,
                1.0F,
                1.0F,
                repeat,
                ModConfig.bellRingDelay,
                ISound.AttenuationType.NONE,
                0.0F,
                0.0F,
                0.0F);
    }
}
