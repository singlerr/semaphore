/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.compat.packets;

import de.maxhenkel.voicechat.voice.common.PlayerSoundPacket;
import io.github.singlerr.semaphore.utils.SerializationUtils;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class PsuedoPlayerSoundPacket extends PlayerSoundPacket {

    private UUID superListener;

    public PsuedoPlayerSoundPacket() {
        super();
    }

    public PsuedoPlayerSoundPacket(PlayerSoundPacket packet, UUID superListener) {
        super(
                packet.getChannelId(),
                packet.getSender(),
                packet.getData(),
                packet.getSequenceNumber(),
                packet.isWhispering(),
                packet.getDistance(),
                packet.getCategory());
        this.superListener = superListener;
    }

    @Override
    public PlayerSoundPacket fromBytes(PacketBuffer buf) {
        PlayerSoundPacket packet = super.fromBytes(buf);
        superListener = SerializationUtils.readUUID(buf);

        return new PsuedoPlayerSoundPacket(packet, superListener);
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        SerializationUtils.writeUUID(buf, superListener);
    }
}
