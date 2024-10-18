/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.interactors;

import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.call.CallState;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.manager.base.BaseCallConnectionManager;
import io.github.singlerr.semaphore.interactors.admin.manager.data.CallConnectionEntity;
import io.github.singlerr.semaphore.interactors.admin.manager.data.ConnectionState;
import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;

import java.util.List;
import java.util.stream.Collectors;

public final class VoicechatCallConnectionManager extends BaseCallConnectionManager {

    public VoicechatCallConnectionManager(
            DatabaseGateway database, CallConnectionHandler callConnectionHandler, CallConnectionPresenter presenter) {
        super(database, callConnectionHandler, presenter);
    }

    @Override
    public List<CallConnectionEntity> getAll() {
        return callConnectionHandler.getAll().stream()
                .map(e -> new CallConnectionEntity(
                        e.getId(),
                        e.getCallerId(),
                        e.getCalleeId(),
                        e.getState() == CallState.ALIVE ? ConnectionState.ALIVE : ConnectionState.DEAD))
                .collect(Collectors.toList());
    }
}
