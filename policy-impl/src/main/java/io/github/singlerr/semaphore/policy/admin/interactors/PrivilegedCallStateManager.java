/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.interactors;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.admin.manager.CallConnectionManager;
import io.github.singlerr.semaphore.interactors.admin.manager.CallStateManager;
import io.github.singlerr.semaphore.interactors.admin.manager.data.Call;
import io.github.singlerr.semaphore.interactors.admin.manager.data.CallConnectionEntity;
import io.github.singlerr.semaphore.interactors.admin.manager.data.ConnectionState;
import io.github.singlerr.semaphore.interactors.admin.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PrivilegedCallStateManager implements CallStateManager {

    private final DatabaseGateway database;
    private final CallConnectionManager callConnectionManager;
    private final ErrorPresenter errorPresenter;

    private final Map<UUID, Peer> connections;

    public PrivilegedCallStateManager(
            DatabaseGateway database, CallConnectionManager callConnectionManager, ErrorPresenter errorPresenter) {
        this.database = database;
        this.callConnectionManager = callConnectionManager;
        this.errorPresenter = errorPresenter;
        this.connections = new ConcurrentHashMap<>();
    }

    @Override
    public Call openCall(UUID callerId, UUID calleeId) {
        Entity caller = this.database.getById(callerId);
        Entity callee = this.database.getById(calleeId);

        if (caller == null || callee == null) return null;

        Optional<Map.Entry<UUID, Peer>> prevCon = this.connections.entrySet().stream()
                .filter(entry -> entry.getValue().getCallerId().equals(callerId)
                        && entry.getValue().getCalleeId().equals(calleeId))
                .findAny();

        if (prevCon.isPresent()) return null;

        CallConnectionEntity con = this.callConnectionManager.open(callerId, calleeId);

        this.connections.put(con.id(), new Peer(callerId, calleeId));
        return new Call(con.id(), con.callerId(), con.calleeId(), con.state());
    }

    @Override
    public Call closeCall(UUID id) {
        Peer p = this.connections.get(id);
        if (p == null) return null;

        CallConnectionEntity con = this.callConnectionManager.getById(id);
        if (con == null) return null;

        this.callConnectionManager.close(id);
        errorPresenter.presentError(new ErrorEntity("error.call.closed" + p.getCallerId() + "|" + p.getCalleeId()));
        return new Call(con.id(), con.callerId(), con.calleeId(), ConnectionState.DEAD);
    }

    @Override
    public Call closeCall(UUID callerId, UUID calleeId) {
        Optional<Map.Entry<UUID, Peer>> con = this.connections.entrySet().stream()
                .filter(entry -> entry.getValue().getCallerId().equals(callerId)
                        && entry.getValue().getCalleeId().equals(calleeId))
                .findAny();
        if (!con.isPresent()) return null;

        this.callConnectionManager.close(con.get().getKey());
        this.connections.remove(con.get().getKey());

        Peer p = con.get().getValue();

        errorPresenter.presentError(new ErrorEntity("error.call.closed" + p.getCallerId() + "|" + p.getCalleeId()));
        return new Call(con.get().getKey(), p.getCallerId(), p.getCalleeId(), ConnectionState.DEAD);
    }

    private static class Peer {

        private final UUID callerId;
        private final UUID calleeId;

        public Peer(UUID callerId, UUID calleeId) {
            this.callerId = callerId;
            this.calleeId = calleeId;
        }

        public UUID getCallerId() {
            return callerId;
        }

        public UUID getCalleeId() {
            return calleeId;
        }
    }
}
