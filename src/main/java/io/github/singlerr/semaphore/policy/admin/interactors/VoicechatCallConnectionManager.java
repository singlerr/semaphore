/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.interactors;

import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.manager.base.BaseCallConnectionManager;
import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;

public final class VoicechatCallConnectionManager extends BaseCallConnectionManager {

    public VoicechatCallConnectionManager(
            DatabaseGateway database, CallConnectionHandler callConnectionHandler, CallConnectionPresenter presenter) {
        super(database, callConnectionHandler, presenter);
    }
}
