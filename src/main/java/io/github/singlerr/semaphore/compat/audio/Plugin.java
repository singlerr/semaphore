/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.compat.audio;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import io.github.singlerr.semaphore.Semaphore;
import lombok.Getter;

@ForgeVoicechatPlugin
public final class Plugin implements VoicechatPlugin {

    @Getter
    private static VoicechatApi api;

    @Override
    public String getPluginId() {
        return Semaphore.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        Plugin.api = api;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(ClientReceiveSoundEvent.class, VoicechatEventListener::onReceiveStaticSound);
    }
}
