package io.github.singlerr.semaphore.network.caller.server;

import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import io.github.singlerr.semaphore.network.NetworkManager;

public final class ServerboundCallRequestPresenter implements CallRequestPresenter {

    private final NetworkManager networkManager;

    public ServerboundCallRequestPresenter(NetworkManager networkManager){
        this.networkManager = networkManager;
    }

    @Override
    public void present(InverseCallRequest request) {

    }
}
