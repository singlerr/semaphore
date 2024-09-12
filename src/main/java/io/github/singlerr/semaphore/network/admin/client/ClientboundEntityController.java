/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.client;

import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.packet.*;

public final class ClientboundEntityController implements EntityController {

    private final NetworkManager networkManager;

    public ClientboundEntityController(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void getEntity(EntityQuery.GetEntity query) {
        this.networkManager.sendToServer(new PacketGetEntity(query.getId()));
    }

    @Override
    public void createEntity(EntityQuery.CreateEntity query) {
        this.networkManager.sendToServer(new PacketCreateEntity(query.getId()));
    }

    @Override
    public void createEntity(EntityQuery.CreateEntityWithState query) {
        this.networkManager.sendToServer(new PacketCreateEntityWithState(
                query.getId(),
                query.getState().getStateId(),
                query.getState().getMissCallCount(),
                query.getState().getEntityType()));
    }

    @Override
    public void deleteEntity(EntityQuery.DeleteEntity query) {
        this.networkManager.sendToServer(new PacketDeleteEntity(query.getId()));
    }

    @Override
    public void updateEntity(EntityQuery.UpdateEntity query) {
        this.networkManager.sendToServer(new PacketUpdateEntity(
                query.getId(),
                query.getState().getStateId(),
                query.getState().getMissCallCount(),
                query.getState().getEntityType()));
    }

    @Override
    public void getAllEntities(EntityQuery.GetAllEntities entities) {
        this.networkManager.sendToServer(new PacketGetAllEntities());
    }
}
