/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callee.controller;

import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController;
import io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.manager.CallResponseManager;
import io.github.singlerr.semaphore.interactors.callee.manager.data.ResponseType;
import io.github.singlerr.semaphore.interactors.caller.manager.CallRequestManager;

public final class RemoteCallResponseController implements CallResponseController {

    private final CallResponseManager responseManager;

    public RemoteCallResponseController(CallResponseManager responseManager){
        this.responseManager = responseManager;
    }

    @Override
    public void reply(CallResponse response) {
        this.responseManager.reply(response.callerId(), response.calleeId(), response.response() == CallResponse.Response.ACCEPT ? ResponseType.ACCEPT : ResponseType.REJECT);
    }
}
