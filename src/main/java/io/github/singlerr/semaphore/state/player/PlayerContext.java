/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.state.player;

import io.github.singlerr.semaphore.state.State;
import io.github.singlerr.semaphore.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PlayerContext implements State<LogicalPlayer> {

    public static final UUID NULL = UUID.randomUUID();

    private LogicalPlayer player;

    @Builder.Default
    private CallState callState = CallState.IDLE;

    @Builder.Default
    @NonNull
    private UUID opponent = NULL;

    @Getter
    @NonNull
    private UUID owner;

    @Getter
    @NonNull
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
        buffer.writeInt(callState.ordinal());
        SerializationUtils.writeUUID(buffer, opponent);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int ordinal = buffer.readInt();
        callState = CallState.values()[ordinal];
        opponent = SerializationUtils.readUUID(buffer);
    }

    @Override
    public boolean equals(State<LogicalPlayer> other) {
        if (other instanceof PlayerContext) {
            return callState == ((PlayerContext) other).callState && opponent == ((PlayerContext) other).opponent;
        }

        return false;
    }

    public void copy(PlayerContext other) {
        this.callState = other.getCallState();
        this.opponent = other.getOpponent();
        this.owner = other.getOwner();
        this.name = other.getName();
        this.volumes = new HashMap<>(other.getVolumes());
        this.missCalls = new HashMap<>(other.getMissCalls());
    }

    public synchronized void setOpponent(UUID opponent) {
        this.opponent = opponent;
    }

    public synchronized UUID getOpponent() {
        return opponent;
    }

    public enum CallState {
        IDLE,
        // Player is sending call request to another
        CALLING,
        // Player now in call
        IN_CALL,
        UNAVAILABLE
    }

    public enum CallFeedback {
        DENY_IN_CALL,
        DENY_NOT_AVAILABLE,
        ACCEPT
    }
}
