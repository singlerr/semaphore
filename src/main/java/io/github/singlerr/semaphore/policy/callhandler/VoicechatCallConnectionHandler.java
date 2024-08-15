/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callhandler;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import io.github.singlerr.semaphore.callhandler.BaseCallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.call.CallConnection;
import io.github.singlerr.semaphore.interactors.access.call.CallState;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class VoicechatCallConnectionHandler extends BaseCallConnectionHandler {

    private final VoicechatServerApi voicechatServerApi;
    private final Map<UUID, CallConnection> connections;

    public VoicechatCallConnectionHandler(DatabaseGateway database, VoicechatServerApi voicechatServerApi) {
        super(database);
        this.voicechatServerApi = voicechatServerApi;
        this.connections = new HashMap<>();
    }

    @Override
    public CallConnection open(UUID callerId, UUID calleeId) {
        if (database.getById(calleeId) == null || database.getById(calleeId) == null) return null;

        VoicechatConnection caller = voicechatServerApi.getConnectionOf(callerId);
        VoicechatConnection callee = voicechatServerApi.getConnectionOf(calleeId);
        if (caller == null || callee == null) return null;

        Group group = voicechatServerApi
                .groupBuilder()
                .setHidden(true)
                .setName("p2pGroup")
                .setType(Group.Type.ISOLATED)
                .build();
        caller.setGroup(group);
        callee.setGroup(group);

        CallConnection connection = new CallConnection(group.getId(), calleeId, callerId, CallState.ALIVE);
        connections.put(group.getId(), connection);
        return connection;
    }

    @Override
    public CallConnection close(UUID connectionId) {
        CallConnection connection = connections.remove(connectionId);

        Group group = voicechatServerApi.getGroup(connectionId);
        if (group == null) return null;

        if (connection == null) return null;

        VoicechatConnection caller = voicechatServerApi.getConnectionOf(connection.callerId());
        VoicechatConnection callee = voicechatServerApi.getConnectionOf(connection.calleeId());
        if (caller == null || callee == null) return null;

        caller.setGroup(null);
        callee.setGroup(null);

        return new CallConnection(connection.id(), connection.calleeId(), connection.callerId(), CallState.DEAD);
    }

    @Override
    public CallConnection getById(UUID connectionId) {
        return connections.get(connectionId);
    }
}
