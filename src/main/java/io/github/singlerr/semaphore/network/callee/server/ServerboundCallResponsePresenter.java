/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.callee.server;

import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.network.NetworkManager;

public final class ServerboundCallResponsePresenter implements CallResponsePresenter {

    private final NetworkManager networkManager;

    public ServerboundCallResponsePresenter(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void present(CallResponse entity) {}

    @Override
    public void error(Error entity) {}
}
