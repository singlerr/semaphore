/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server;

import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery;

public final class ServerboundCallStateController implements CallStateController {

    private final CallStateController source;

    public ServerboundCallStateController(CallStateController callStateController) {
        this.source = callStateController;
    }

    @Override
    public void openCall(CallStateQuery.OpenCall query) {
        this.source.openCall(query);
    }

    @Override
    public void closeCall(CallStateQuery.CloseCall query) {
        this.source.closeCall(query);
    }

    @Override
    public void closeCall(CallStateQuery.CloseCallById query) {
        this.source.closeCall(query);
    }
}
