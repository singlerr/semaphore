/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin;

import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.AdminInteractor;
import io.github.singlerr.semaphore.interactors.admin.manager.CallConnectionManager;
import io.github.singlerr.semaphore.interactors.admin.manager.CallStateManager;
import io.github.singlerr.semaphore.interactors.admin.manager.EntityManager;
import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.policy.admin.interactors.PlayerManager;
import io.github.singlerr.semaphore.policy.admin.interactors.PrivilegedCallStateManager;
import io.github.singlerr.semaphore.policy.admin.interactors.VoicechatCallConnectionManager;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;

public final class SimpleAdminInteractor implements AdminInteractor {

    private final VoicechatCallConnectionManager callConnectionManager;
    private final PrivilegedCallStateManager callStateManager;
    private final PlayerManager entityManager;

    private final CallConnectionPresenter callConnectionPresenter;
    private final EntityPresenter entityPresenter;

    public SimpleAdminInteractor(
            DatabaseGateway database,
            CallConnectionHandler callConnectionHandler,
            CallConnectionPresenter callConnectionPresenter,
            EntityPresenterAdapter entityPresenter) {
        this.callConnectionManager =
                new VoicechatCallConnectionManager(database, callConnectionHandler, callConnectionPresenter);
        this.callStateManager = new PrivilegedCallStateManager(database, callConnectionManager, entityPresenter);
        this.entityManager = new PlayerManager(database);
        this.callConnectionPresenter = callConnectionPresenter;
        this.entityPresenter = entityPresenter;
    }

    @Override
    public CallConnectionManager getConnectionManager() {
        return callConnectionManager;
    }

    @Override
    public CallStateManager getStateManager() {
        return callStateManager;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public CallConnectionPresenter getConnectionPresenter() {
        return callConnectionPresenter;
    }

    @Override
    public EntityPresenter getEntityPresenter() {
        return entityPresenter;
    }
}
