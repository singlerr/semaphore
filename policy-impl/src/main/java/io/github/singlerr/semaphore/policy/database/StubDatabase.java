/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.database;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import java.util.UUID;

public final class StubDatabase implements DatabaseGateway {
    @Override
    public void update(UUID id, Entity state) {}

    @Override
    public void create(UUID id, Entity newState) {}

    @Override
    public Entity create() {
        return null;
    }

    @Override
    public void delete(UUID id) {}

    @Override
    public Entity getById(UUID id) {
        return null;
    }
}
