/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.interactors;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.manager.base.BaseCallStateManager;

public final class PrivilegedCallStateManager extends BaseCallStateManager {

    public PrivilegedCallStateManager(DatabaseGateway database) {
        super(database);
    }
}
