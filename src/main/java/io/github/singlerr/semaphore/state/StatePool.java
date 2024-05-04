/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.state;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class StatePool {

    private final ConcurrentMap<UUID, State<?>> states = new ConcurrentHashMap<>();

    public void submit(UUID uuid, State<?> state) {
        log.info("Submitting state {} to state pool", uuid);

        if (!states.containsKey(uuid)) {
            states.put(uuid, state);
            return;
        }

        State<?> prev = states.get(uuid);
        if (!prev.equals(state)) {
            states.put(uuid, state);
        } else {
            log.info("Ignoring duplicate state {}", uuid);
        }
    }

    public Set<Map.Entry<UUID, State<?>>> getStates() {
        return states.entrySet();
    }

    public Optional<State<?>> get(UUID uuid) {
        return Optional.ofNullable(states.get(uuid));
    }
}
