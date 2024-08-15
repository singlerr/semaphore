/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callee.controller;

import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.callee.controller.base.BaseCallResponseController;

public final class SimpleCallResponseController extends BaseCallResponseController {

    public SimpleCallResponseController(CalleeInteractor interactor) {
        super(interactor);
    }
}
