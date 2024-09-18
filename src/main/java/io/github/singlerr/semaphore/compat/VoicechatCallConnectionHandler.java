/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.compat;

import com.google.common.collect.Lists;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import io.github.singlerr.semaphore.Constants;
import io.github.singlerr.semaphore.callhandler.BaseCallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.call.CallConnection;
import io.github.singlerr.semaphore.interactors.access.call.CallState;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import java.util.HashMap;
import java.util.List;
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
                .setName(Constants.P2P_GROUP)
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

        VoicechatConnection caller = voicechatServerApi.getConnectionOf(connection.getCallerId());
        VoicechatConnection callee = voicechatServerApi.getConnectionOf(connection.getCalleeId());
        if (caller == null || callee == null) return null;

        caller.setGroup(null);
        callee.setGroup(null);

        return new CallConnection(
                connection.getId(), connection.getCalleeId(), connection.getCallerId(), CallState.DEAD);
    }

    @Override
    public CallConnection getById(UUID connectionId) {
        return connections.get(connectionId);
    }

    @Override
    public List<CallConnection> getAll() {
        return Lists.newArrayList(connections.values());
    }
}
