/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.compat;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.ClientVoicechatInitializationEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import org.jetbrains.annotations.Nullable;

@ForgeVoicechatPlugin
public class ModVoicechatPlugin implements VoicechatPlugin {

    private static VoicechatApi voicechatApi;

    @Nullable
    private static VoicechatServerApi voicechatServerApi;

    @Override
    public String getPluginId() {
        return Semaphore.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        voicechatApi = api;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(ClientVoicechatInitializationEvent.class, this::onClientStarted);
    }

    private void onClientStarted(ClientVoicechatInitializationEvent event) {
        ClientResources.StaticResources.VOICECHAT_CLIENT_API = event.getVoicechat();
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        voicechatServerApi = event.getVoicechat();
        Semaphore.serverStarted(event);
    }
}
