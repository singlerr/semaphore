/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.client;

import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;

public final class ClientboundCallConnectionPresenter implements CallConnectionPresenter {

    private final CallConnectionPresenter source;

    public ClientboundCallConnectionPresenter(CallConnectionPresenter source) {
        this.source = source;
    }

    @Override
    public void present(PresentableCallConnection entity) {
        this.source.present(entity);
    }

    @Override
    public void presentError(ErrorEntity error) {
        this.source.presentError(error);
    }
}
