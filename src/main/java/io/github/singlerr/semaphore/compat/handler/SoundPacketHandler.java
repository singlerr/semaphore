/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.compat.handler;

import de.maxhenkel.voicechat.api.events.EntitySoundPacketEvent;
import io.github.singlerr.semaphore.compat.packets.PsuedoPlayerSoundPacket;
import java.util.function.Consumer;

public class SoundPacketHandler implements Consumer<EntitySoundPacketEvent> {
    @Override
    public void accept(EntitySoundPacketEvent event) {
        if (!(event.getPacket() instanceof PsuedoPlayerSoundPacket)) return;

        PsuedoPlayerSoundPacket packet = (PsuedoPlayerSoundPacket) event.getPacket();
    }
}
