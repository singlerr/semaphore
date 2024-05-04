/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.compat;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EntitySoundPacketEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.compat.handler.SoundPacketHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
@ForgeVoicechatPlugin
public class VoicechatCompatPlugin implements VoicechatPlugin {

    @Override
    public String getPluginId() {
        return Semaphore.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {}

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(EntitySoundPacketEvent.class, new SoundPacketHandler());
    }
}
