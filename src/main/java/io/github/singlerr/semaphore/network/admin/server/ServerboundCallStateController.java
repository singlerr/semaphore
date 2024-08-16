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
    public void getCallState(CallStateQuery.GetCallState query) {
        this.source.getCallState(query);
    }

    @Override
    public void setCallState(CallStateQuery.SetCallState query) {
        this.source.setCallState(query);
    }
}
