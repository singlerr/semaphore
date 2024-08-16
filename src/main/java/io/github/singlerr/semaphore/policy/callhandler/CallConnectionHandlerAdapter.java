/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callhandler;

import io.github.singlerr.semaphore.interactors.access.call.CallConnection;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import java.util.UUID;

public final class CallConnectionHandlerAdapter implements CallConnectionHandler {

    private CallConnectionHandler adapter;

    public CallConnectionHandlerAdapter() {}

    public CallConnectionHandlerAdapter(CallConnectionHandler adapter) {
        this.adapter = adapter;
    }

    public void setAdapter(CallConnectionHandler adapter) {
        this.adapter = adapter;
    }

    @Override
    public CallConnection open(UUID callerId, UUID calleeId) {
        if (this.adapter == null) return null;
        return this.adapter.open(callerId, calleeId);
    }

    @Override
    public CallConnection close(UUID connectionId) {
        if (this.adapter == null) return null;
        return this.adapter.close(connectionId);
    }

    @Override
    public CallConnection getById(UUID connectionId) {
        if (this.adapter == null) return null;
        return this.adapter.getById(connectionId);
    }
}
