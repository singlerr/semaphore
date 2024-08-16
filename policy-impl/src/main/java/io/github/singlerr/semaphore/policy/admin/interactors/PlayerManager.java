/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.interactors;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.manager.base.BaseEntityManager;

public final class PlayerManager extends BaseEntityManager {
    public PlayerManager(DatabaseGateway database) {
        super(database);
    }
}
