/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.database;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerDatabase implements DatabaseGateway {

    private final Map<UUID, Entity> entities;

    public PlayerDatabase() {
        this.entities = new HashMap<>();
    }

    @Override
    public void update(UUID id, Entity state) {
        entities.put(id, state);
    }

    @Override
    public void create(UUID id, Entity newState) {
        entities.put(id, newState);
    }

    @Override
    public Entity create() {
        return new Entity(UUID.randomUUID(), 0);
    }

    @Override
    public void delete(UUID id) {
        entities.remove(id);
    }

    @Override
    public Entity getById(UUID id) {
        return entities.get(id);
    }
}
