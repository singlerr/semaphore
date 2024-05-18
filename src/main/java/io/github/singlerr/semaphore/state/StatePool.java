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

        states.put(uuid, state);
        log.info("Updating state {}", uuid);
    }

    public void remove(UUID id) {
        states.remove(id);
    }

    public Set<Map.Entry<UUID, State<?>>> getStates() {
        return states.entrySet();
    }

    public Optional<State<?>> get(UUID uuid) {
        return Optional.ofNullable(states.get(uuid));
    }

    public <T extends State<?>> Optional<T> get(UUID uuid, Class<T> clazz) {
        return Optional.ofNullable(states.get(uuid)).map(clazz::cast);
    }
}
