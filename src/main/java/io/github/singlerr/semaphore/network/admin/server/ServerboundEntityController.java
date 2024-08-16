/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server;

import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;

public final class ServerboundEntityController implements EntityController {

    private final EntityController source;

    public ServerboundEntityController(EntityController source) {
        this.source = source;
    }

    @Override
    public void getEntity(EntityQuery.GetEntity query) {
        this.source.getEntity(query);
    }

    @Override
    public void createEntity(EntityQuery.CreateEntity query) {
        this.source.createEntity(query);
    }

    @Override
    public void deleteEntity(EntityQuery.DeleteEntity query) {
        this.source.deleteEntity(query);
    }

    @Override
    public void getAllEntities(EntityQuery.GetAllEntities query) {
        this.source.getAllEntities(query);
    }
}
