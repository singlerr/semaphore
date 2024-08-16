/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.caller.controllers;

import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController;
import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest;
import io.github.singlerr.semaphore.interactors.caller.manager.CallRequestManager;
import io.github.singlerr.semaphore.policy.callee.controller.RemoteCallResponseController;

public final class RemoteCallRequestController implements CallRequestController {

    private final CallRequestManager requestManager;

    public RemoteCallRequestController(CallRequestManager requestManager){
        this.requestManager = requestManager;
    }

    @Override
    public void request(CallRequest request) {
        this.requestManager.request(new io.github.singlerr.semaphore.interactors.caller.manager.data.CallRequest(request.callerId(), request.calleeId()));
    }
}
