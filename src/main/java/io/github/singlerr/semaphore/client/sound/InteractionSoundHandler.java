/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.client.sound;

import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;

import java.util.List;

public final class InteractionSoundHandler implements EntityPresenter, CallResponsePresenter, CallRequestPresenter {

    @Override
    public void present(InverseCallRequest request) {
    }

    @Override
    public void present(CallResponse entity) {
    }

    @Override
    public void error(Error entity) {
    }

    @Override
    public void present(PresentableEntity entity) {
    }

    @Override
    public void present(List<PresentableEntity> entities) {
    }

    @Override
    public void presentError(ErrorEntity error) {
    }
}
