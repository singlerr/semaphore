/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.client;

import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error;

public final class ClientboundErrorPresenter implements ErrorPresenter {

    private final ErrorPresenter source;

    public ClientboundErrorPresenter(ErrorPresenter source) {
        this.source = source;
    }

    @Override
    public void present(Error error) {
        this.source.present(error);
    }
}
