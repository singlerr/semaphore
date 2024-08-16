/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.callee.server;

import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController;
import io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse;

public final class ServerboundCallResponseController implements CallResponseController {

    private final CallResponseController source;

    public ServerboundCallResponseController(CallResponseController source) {
        this.source = source;
    }

    @Override
    public void reply(CallResponse response) {
        this.source.reply(response);
    }
}
