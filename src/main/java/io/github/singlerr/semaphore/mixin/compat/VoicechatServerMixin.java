/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.compat;

import de.maxhenkel.voicechat.voice.common.GroupSoundPacket;
import de.maxhenkel.voicechat.voice.common.MicPacket;
import de.maxhenkel.voicechat.voice.common.PlayerState;
import de.maxhenkel.voicechat.voice.common.SoundPacket;
import de.maxhenkel.voicechat.voice.server.ClientConnection;
import de.maxhenkel.voicechat.voice.server.PlayerStateManager;
import de.maxhenkel.voicechat.voice.server.Server;
import de.maxhenkel.voicechat.voice.server.ServerWorldUtils;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.state.State;
import io.github.singlerr.semaphore.state.StatePool;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Server.class)
public abstract class VoicechatServerMixin {

    @Shadow
    public abstract void broadcast(
            Collection<EntityPlayerMP> players,
            SoundPacket<?> packet,
            @Nullable EntityPlayerMP sender,
            @Nullable PlayerState senderState,
            @Nullable UUID groupId,
            String source);

    @Shadow
    public abstract double getBroadcastRange(float minRange);

    @Shadow
    @Final
    private PlayerStateManager playerStateManager;

    @Shadow
    @Nullable
    public abstract ClientConnection getConnection(UUID playerID);

    @Shadow
    public abstract void sendSoundPacket(
            @Nullable EntityPlayerMP sender,
            @Nullable PlayerState senderState,
            EntityPlayerMP receiver,
            PlayerState receiverState,
            @Nullable ClientConnection connection,
            SoundPacket<?> soundPacket,
            String source);

    @Inject(
            method = "processProximityPacket",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lde/maxhenkel/voicechat/voice/server/Server;broadcast(Ljava/util/Collection;Lde/maxhenkel/voicechat/voice/common/SoundPacket;Lnet/minecraft/entity/player/EntityPlayerMP;Lde/maxhenkel/voicechat/voice/common/PlayerState;Ljava/util/UUID;Ljava/lang/String;)V",
                            shift = At.Shift.BEFORE),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            remap = false)
    private void semaphore$transformPacket(
            PlayerState senderState,
            EntityPlayerMP sender,
            MicPacket packet,
            CallbackInfo ci,
            UUID groupId,
            float distance,
            SoundPacket soundPacket,
            String source) {

        if (soundPacket == null) return;

        UUID senderUUID = sender.getUniqueID();

        StatePool pool = ServerRegistries.getStatePool();

        Optional<State> opt = pool.get(senderUUID);
        if (!opt.isPresent()) return;

        if (!(opt.get() instanceof PlayerContext)) return;

        PlayerContext ctx = (PlayerContext) opt.get();

        if (ctx.getCallState() != PlayerContext.CallState.IN_CALL) return;

        UUID opponentUUID = ctx.getOpponent();

        Optional<EntityPlayer> opponent = sender.getServerWorld().playerEntities.stream()
                .filter(p -> p.getUniqueID().equals(opponentUUID))
                .findFirst();

        if (!opponent.isPresent()) return;

        GroupSoundPacket directPacket = new GroupSoundPacket(
                senderState.getUuid(), senderState.getUuid(), packet.getData(), packet.getSequenceNumber(), null);

        PlayerState opponentState = playerStateManager.getState(opponentUUID);

        if (opponentState == null) return;

        ClientConnection connection = getConnection(opponentState.getUuid());

        sendSoundPacket(
                sender, senderState, (EntityPlayerMP) opponent.get(), opponentState, connection, directPacket, "group");

        Collection<EntityPlayerMP> receivers = ServerWorldUtils.getPlayersInRange(
                sender.getServerWorld(),
                sender.getPositionVector(),
                this.getBroadcastRange(distance),
                (p) -> !p.getUniqueID().equals(sender.getUniqueID())
                        && !p.getUniqueID().equals(opponentUUID));

        broadcast(receivers, soundPacket, sender, senderState, groupId, source);

        ci.cancel();
    }
}
