/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.caller.server;

import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController;
import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest;

public final class ServerboundCallRequestController implements CallRequestController {

    private final CallRequestController source;

    public ServerboundCallRequestController(CallRequestController source) {
        this.source = source;
    }

    @Override
    public void request(CallRequest request) {
        this.source.request(request);
    }
}
