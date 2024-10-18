/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.client;

import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;

import java.util.List;

public final class ClientboundEntityPresenter implements EntityPresenter {

    private final EntityPresenter source;

    public ClientboundEntityPresenter(EntityPresenter source) {
        this.source = source;
    }

    @Override
    public void present(PresentableEntity entity) {
        this.source.present(entity);
    }

    @Override
    public void present(List<PresentableEntity> entities) {
        this.source.present(entities);
    }

    @Override
    public void presentError(ErrorEntity error) {
        this.source.presentError(error);
    }
}
