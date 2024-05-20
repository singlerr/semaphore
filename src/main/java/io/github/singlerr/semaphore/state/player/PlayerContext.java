/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.state.player;

import io.github.singlerr.semaphore.state.State;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.network.PacketBuffer;

@Setter
@Getter
@Builder
public class PlayerContext implements State {

    public static final UUID NULL = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Builder.Default
    private CallState callState = CallState.IDLE;

    @Builder.Default
    private UUID opponent = NULL;

    @Getter
    private UUID owner;

    @Getter
    private String name;

    @Getter
    @Builder.Default
    @NonNull
    private Map<UUID, Float> volumes = new HashMap<>();

    @Getter
    @Builder.Default
    @NonNull
    private Map<UUID, AtomicInteger> missCalls = new HashMap<>();

    private boolean usingPhone = false;

    @Override
    public void serialize(ByteBuf buffer) {
        PacketBuffer wrapper = new PacketBuffer(buffer);
        wrapper.writeEnumValue(callState);
        wrapper.writeUniqueId(opponent);
        wrapper.writeUniqueId(owner);
        wrapper.writeString(name);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        PacketBuffer wrapper = new PacketBuffer(buffer);
        callState = wrapper.readEnumValue(CallState.class);
        opponent = wrapper.readUniqueId();
        owner = wrapper.readUniqueId();
        name = wrapper.readString(50);
    }

    public void update(PlayerContext other) {
        this.callState = other.getCallState();
        this.opponent = other.getOpponent();
        this.owner = other.getOwner();
        this.name = other.getName();
        this.volumes = new HashMap<>(other.getVolumes());
        this.missCalls = new HashMap<>(other.getMissCalls());
    }

    public AtomicInteger getMissCount(UUID id) {

        AtomicInteger count;
        if (!missCalls.containsKey(id)) {
            count = new AtomicInteger(0);
            missCalls.put(id, count);
        } else {
            count = missCalls.get(id);
        }

        return count;
    }

    public synchronized UUID getOpponent() {
        return opponent;
    }

    public static PlayerContext from(ByteBuf buffer) {
        PacketBuffer wrapper = new PacketBuffer(buffer);
        CallState callState = wrapper.readEnumValue(CallState.class);
        UUID opponent = wrapper.readUniqueId();
        UUID owner = wrapper.readUniqueId();
        String name = wrapper.readString(50);
        return PlayerContext.builder()
                .callState(callState)
                .opponent(opponent)
                .owner(owner)
                .name(name)
                .build();
    }

    public static PlayerContext from(UUID id) {
        return PlayerContext.builder().owner(id).name(id.toString()).build();
    }

    public enum CallState {
        IDLE,
        // Player is sending call request to another
        CALLING,
        // Player now in call
        IN_CALL,
        // Player receiving call
        RECEIVING_CALL,
        UNAVAILABLE
    }

    public enum CallRejectReason {
        PLAYER_NOT_ONLINE,
        PLAYER_REJECTED,
        PLAYER_IN_CALL,
        OTHER
    }

    public enum CallFeedback {
        DENY_IN_CALL,
        DENY_NOT_AVAILABLE,
        ACCEPT
    }
}
