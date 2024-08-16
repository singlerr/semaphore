/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.client;

import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;

public final class ClientboundCallRequestPresenter implements CallRequestPresenter {

    private final CallRequestPresenter source;

    public ClientboundCallRequestPresenter(CallRequestPresenter source) {
        this.source = source;
    }

    @Override
    public void present(InverseCallRequest request) {
        this.source.present(request);
    }
}
