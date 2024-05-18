/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.sound;

import io.github.singlerr.semaphore.config.ModConfig;
import io.github.singlerr.semaphore.events.*;
import io.github.singlerr.semaphore.mixin.core.SoundHandlerAccessor;
import io.github.singlerr.semaphore.mixin.core.SoundManagerAccessor;
import io.github.singlerr.semaphore.registries.ClientRegistries;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.EventPool;
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Log4j2
@SideOnly(Side.CLIENT)
@UtilityClass
public class ClientSoundHandler {

    private final Map<ResourceLocation, ISound> sounds = new HashMap<>();

    public void register(EventPool eventPool) {
        eventPool.subscribe(ReceivingCallEvent.class, ClientSoundHandler::handleRing);
        eventPool.subscribe(StopSoundCommand.class, ClientSoundHandler::stopSound);
        eventPool.subscribe(PlaySoundCommand.class, ClientSoundHandler::playSound);
        eventPool.subscribe(SendingCallEvent.class, ClientSoundHandler::handlePhoneCall);
        eventPool.subscribe(CallAcceptedEvent.class, ClientSoundHandler::handleAcceptPhoneCall);
        eventPool.subscribe(InComingCallFeedbackEvent.class, ClientSoundHandler::handleInComingCallFeedback);
        eventPool.subscribe(OutGoingCallFeedbackEvent.class, ClientSoundHandler::handleOutComingCallFeedback);
        eventPool.subscribe(CallClosedEvent.class, ClientSoundHandler::handleCallClosed);
    }

    private void handleRing(ReceivingCallEvent event) {
        SoundEvent sound =
                ModConfig.bellRing ? ClientRegistries.SOUND_PHONE_BELL : ClientRegistries.SOUND_PHONE_VIBRATE;
        stopAll();
        playRepeatable(sound);
    }

    private void handlePhoneCall(SendingCallEvent event) {
        stopAll();
        playRepeatable(ClientRegistries.SOUND_CALLING);
    }

    private void handleInComingCallFeedback(InComingCallFeedbackEvent event) {
        stopAll();
        stopRepeatable(ClientRegistries.SOUND_CALLING);
        if (event.getFeedback() != PlayerContext.CallFeedback.ACCEPT) {
            playNonRepeatable(ClientRegistries.MISS_CALL_SOUND);
        } else {
            playNonRepeatable(ClientRegistries.SOUND_CALL_YES);
        }
    }

    private void handleOutComingCallFeedback(OutGoingCallFeedbackEvent event) {
        stopAll();
        if (event.getFeedback() == PlayerContext.CallFeedback.ACCEPT) {
            playNonRepeatable(ClientRegistries.SOUND_CALL_YES);

            return;
        }
        playNonRepeatable(ClientRegistries.SOUND_CALL_NO);
    }

    private void handleCallClosed(CallClosedEvent event) {
        stopAll();
        stopRepeatable(ClientRegistries.SOUND_CALLING);
        stopRepeatable(ClientRegistries.SOUND_PHONE_BELL);
        playNonRepeatable(ClientRegistries.SOUND_CALL_OFF);
    }

    private void playNonRepeatable(ResourceLocationBuilder resourceLocationBuilder) {
        try {
            AudioPlayer.play(resourceLocationBuilder);
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    private void handleAcceptPhoneCall(CallAcceptedEvent event) {
        stopRepeatable(ClientRegistries.SOUND_CALLING);
    }

    private void stopSound(StopSoundCommand command) {
        stopRepeatable(ClientRegistries.SOUND_CALLING);
        stopRepeatable(ClientRegistries.SOUND_PHONE_BELL);
    }

    private void playNonRepeatable(SoundEvent soundEvent) {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(soundEvent, 1.0F));
    }

    private void playRepeatable(SoundEvent soundEvent) {
        ISound sound;

        if (sounds.containsKey(soundEvent.getSoundName())) {
            sound = sounds.get(soundEvent.getSoundName());
        } else {
            sound = getRepeatable(soundEvent, true);
            sounds.put(soundEvent.getSoundName(), sound);
        }

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

    private void playSound(PlaySoundCommand command) {
        playNonRepeatable(command.getSound());
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
