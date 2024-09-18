/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.dfa;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NFA implements Cloneable {

    // key: input for state machine, value: next state if machine can transit
    // key is packed by 32bit integer, key = currentState & 0xFF << 8 | responseType.ordinal() & 0xFF
    private final Map<Integer, Integer> encodedStateFunction;

    private final Map<Integer, PlayerState> encoder;

    public NFA() {
        this.encodedStateFunction = new HashMap<>();
        this.encoder = new HashMap<>();
    }

    public NFA(Map<Integer, Integer> encodedStateFunction, Map<Integer, PlayerState> encoder) {
        this.encodedStateFunction = encodedStateFunction;
        this.encoder = encoder;
    }

    public Optional<Integer> consume(int currentState, PlayerInput input) {
        int packedInput = packInput(currentState, input);

        // This won't happen but handle it
        if (!encodedStateFunction.containsKey(packedInput)) return Optional.empty();

        int newState = encodedStateFunction.get(packedInput);

        return Optional.of(newState);
    }

    public PlayerState decode(int state) {
        if (!encoder.containsKey(state)) return null;
        return encoder.get(state);
    }

    public int encode(PlayerState state) {
        return encoder.entrySet().stream()
                .filter(e -> e.getValue() == state)
                .findAny()
                .map(Map.Entry::getKey)
                .get();
    }

    private static int packInput(int state, PlayerInput type) {
        return (state & 0xFF) << 8 | type.ordinal() & 0xFF;
    }

    public static class Builder {

        private final Map<Integer, Integer> encodedStateFunction;
        private final Map<Integer, PlayerState> encoder;

        public Builder() {
            this.encodedStateFunction = new HashMap<>();
            this.encoder = new HashMap<>();
        }

        public Builder encode(int state, PlayerState encodedState) {
            encoder.put(state, encodedState);
            return this;
        }

        public Builder transit(int state, PlayerInput input, int newState) {
            encodedStateFunction.put(packInput(state, input), newState);
            return this;
        }

        public Builder transit(int state, PlayerInput[] input, int newState) {
            for (PlayerInput playerInput : input) {
                encodedStateFunction.put(packInput(state, playerInput), newState);
            }
            return this;
        }

        public NFA build() {
            return new NFA(encodedStateFunction, encoder);
        }
    }

    @Override
    public NFA clone() {
        return new NFA(new HashMap<>(encodedStateFunction), new HashMap<>(encoder));
    }
}
