/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.state.player;

import io.github.singlerr.semaphore.state.State;
import io.github.singlerr.semaphore.utils.SerializationUtils;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
    private Map<UUID, Double> volumes = new HashMap<>();

    private boolean usingPhone = false;

    @Override
    public void apply(LogicalPlayer logicalPlayer) {}

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

    public synchronized void setOpponent(UUID opponent) {
        this.opponent = opponent;
    }

    public synchronized UUID getOpponent() {
        return opponent;
    }

    public enum CallState {
        IDLE,
        CALLING,
        IN_CALL,
        UNAVAILABLE
    }
}
