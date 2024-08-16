/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server;

import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.network.NetworkManager;

import java.util.List;

public final class ServerboundEntityPresenter implements EntityPresenter {

    private final NetworkManager networkManager;

    public ServerboundEntityPresenter(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void present(PresentableEntity entity) {}

    @Override
    public void presentError(ErrorEntity error) {}

    @Override
    public void present(List<PresentableEntity> entities) {
        
    }
}
