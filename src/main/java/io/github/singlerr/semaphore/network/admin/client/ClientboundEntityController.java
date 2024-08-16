/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.client;

import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.packet.PacketCreateEntity;
import io.github.singlerr.semaphore.network.admin.packet.PacketDeleteEntity;
import io.github.singlerr.semaphore.network.admin.packet.PacketGetAllEntities;
import io.github.singlerr.semaphore.network.admin.packet.PacketGetEntity;

public final class ClientboundEntityController implements EntityController {

    private final NetworkManager networkManager;

    public ClientboundEntityController(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void getEntity(EntityQuery.GetEntity query) {
        this.networkManager.sendToServer(new PacketGetEntity(query.id()));
    }

    @Override
    public void createEntity(EntityQuery.CreateEntity query) {
        this.networkManager.sendToServer(new PacketCreateEntity(query.id()));
    }

    @Override
    public void deleteEntity(EntityQuery.DeleteEntity query) {
        this.networkManager.sendToServer(new PacketDeleteEntity(query.id()));
    }

    @Override
    public void getAllEntities() {
        this.networkManager.sendToServer(new PacketGetAllEntities());
    }
}
