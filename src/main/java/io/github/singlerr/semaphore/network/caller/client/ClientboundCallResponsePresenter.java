/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.client;

import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;

public class ClientboundCallResponsePresenter implements CallResponsePresenter {

    private final CallResponsePresenter source;

    public ClientboundCallResponsePresenter(CallResponsePresenter source) {
        this.source = source;
    }

    @Override
    public void present(CallResponse entity) {
        this.source.present(entity);
    }

    @Override
    public void error(Error entity) {
        this.source.error(entity);
    }
}
