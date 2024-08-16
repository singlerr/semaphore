/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.network.admin.server;

import io.github.singlerr.semaphore.interactors.admin.controller.CallConnectionController;
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallConnectionQuery;

public final class ServerboundCallConnectionController implements CallConnectionController {

    private final CallConnectionController source;

    public ServerboundCallConnectionController(CallConnectionController source) {
        this.source = source;
    }

    @Override
    public void openConnection(CallConnectionQuery.OpenConnection query) {
        this.source.openConnection(query);
    }

    @Override
    public void closeConnection(CallConnectionQuery.CloseConnection query) {
        this.source.closeConnection(query);
    }

    @Override
    public void getConnection(CallConnectionQuery.GetConnection query) {
        this.source.getConnection(query);
    }
}
