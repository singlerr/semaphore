/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server;

import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;
import io.github.singlerr.semaphore.network.NetworkManager;

public final class ServerboundCallConnectionPresenter implements CallConnectionPresenter {

    private final NetworkManager networkManager;

    public ServerboundCallConnectionPresenter(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void present(PresentableCallConnection entity) {}

    @Override
    public void presentError(ErrorEntity error) {}
}
