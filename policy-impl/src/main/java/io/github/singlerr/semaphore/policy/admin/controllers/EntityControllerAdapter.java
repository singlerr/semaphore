/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.controllers;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.access.database.Entity;
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
        Entity entity = this.database.getById(query.id());
        if (entity == null) {
            this.entityPresenter.presentError(new ErrorEntity("entity.not.found"));
            return;
        }

        this.entityPresenter.present(new PresentableEntity(
                entity.id(),
                new PresentableEntity.State(
                        entity.state().stateId(), entity.state().missCallCount())));
    }

    @Override
    public void createEntity(EntityQuery.CreateEntity query) {
        this.database.create(query.id());
    }

    @Override
    public void deleteEntity(EntityQuery.DeleteEntity query) {
        this.database.delete(query.id());
    }

    @Override
    public void getAllEntities(EntityQuery.GetAllEntities query) {
        this.entityPresenter.present(this.database.getAll().stream()
                .map(e -> {
                    PresentableEntity entity = new PresentableEntity(
                            e.id(),
                            new PresentableEntity.State(
                                    e.state().stateId(), e.state().missCallCount()));
                    if (query.getContext() != null) entity.setContext(query.getContext());
                    return entity;
                })
                .collect(Collectors.toList()));
    }
}
