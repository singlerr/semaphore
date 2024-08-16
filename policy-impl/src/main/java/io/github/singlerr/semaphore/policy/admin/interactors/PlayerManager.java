/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.interactors;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.manager.base.BaseEntityManager;
import io.github.singlerr.semaphore.interactors.admin.manager.data.CallableEntity;
import java.util.List;
import java.util.stream.Collectors;

public final class PlayerManager extends BaseEntityManager {
    public PlayerManager(DatabaseGateway database) {
        super(database);
    }

    @Override
    public List<CallableEntity> getAll() {
        return database.getAll().stream()
                .map(e -> new CallableEntity(e.id(), e.stateId()))
                .collect(Collectors.toList());
    }
}
