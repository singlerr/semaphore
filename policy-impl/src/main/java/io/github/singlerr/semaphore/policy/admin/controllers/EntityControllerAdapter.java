/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.controllers;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
import io.github.singlerr.semaphore.interactors.access.database.EntityType;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import java.util.stream.Collectors;

public final class EntityControllerAdapter implements EntityController {

    private final DatabaseGateway database;
    private final EntityPresenter entityPresenter;

    public EntityControllerAdapter(DatabaseGateway database, EntityPresenter entityPresenter) {
        this.database = database;
        this.entityPresenter = entityPresenter;
    }

    @Override
    public void getEntity(EntityQuery.GetEntity query) {
        Entity entity = this.database.getById(query.getId());
        if (entity == null) {
            this.entityPresenter.presentError(new ErrorEntity("entity.not.found"));
            return;
        }

        this.entityPresenter.present(new PresentableEntity(
                entity.getId(),
                new PresentableEntity.State(
                        entity.getState().getStateId(),
                        entity.getState().getMissCallCount(),
                        entity.getState().getEntityType())));
    }

    @Override
    public void createEntity(EntityQuery.CreateEntityWithState query) {
        Entity entity = new Entity(
                query.getId(),
                new Entity.State(
                        query.getState().getStateId(),
                        query.getState().getMissCallCount(),
                        io.github.singlerr.semaphore.interactors.access.database.EntityType.valueOf(
                                query.getState().getEntityType().name())));
        this.database.create(query.getId(), entity);

        this.entityPresenter.present(new PresentableEntity(
                entity.getId(),
                new PresentableEntity.State(
                        entity.getState().getStateId(),
                        entity.getState().getMissCallCount(),
                        entity.getState().getEntityType())));
    }

    @Override
    public void createEntity(EntityQuery.CreateEntity query) {
        Entity entity = this.database.create(query.getId());
        this.entityPresenter.present(new PresentableEntity(
                entity.getId(),
                new PresentableEntity.State(
                        entity.getState().getStateId(),
                        entity.getState().getMissCallCount(),
                        entity.getState().getEntityType())));
    }

    @Override
    public void deleteEntity(EntityQuery.DeleteEntity query) {
        this.database.delete(query.getId());
        getAllEntities(new EntityQuery.GetAllEntities());
    }

    @Override
    public void getAllEntities(EntityQuery.GetAllEntities query) {
        this.entityPresenter.present(this.database.getAll().stream()
                .map(e -> {
                    PresentableEntity entity = new PresentableEntity(
                            e.getId(),
                            new PresentableEntity.State(
                                    e.getState().getStateId(),
                                    e.getState().getMissCallCount(),
                                    e.getState().getEntityType()));
                    if (query.getContext() != null) entity.setContext(query.getContext());
                    return entity;
                })
                .collect(Collectors.toList()));
    }

    @Override
    public void updateEntity(EntityQuery.UpdateEntity query) {
        Entity entity = this.database.getById(query.getId());
        if (entity == null) {
            this.entityPresenter.presentError(new ErrorEntity("entity.not.found"));
            return;
        }

        entity = new Entity(
                query.getId(),
                new Entity.State(
                        query.getState().getStateId(),
                        query.getState().getMissCallCount(),
                        EntityType.valueOf(query.getState().getEntityType().name())));
        database.update(query.getId(), entity);

        this.entityPresenter.present(new PresentableEntity(
                entity.getId(),
                new PresentableEntity.State(
                        entity.getState().getStateId(),
                        entity.getState().getMissCallCount(),
                        entity.getState().getEntityType())));
    }
}
