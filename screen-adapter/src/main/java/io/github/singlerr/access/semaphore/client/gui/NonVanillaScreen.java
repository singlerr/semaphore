/* (C) 2024 singlerr */
package io.github.singlerr.access.semaphore.client.gui;

import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.controller.CallConnectionController;
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController;
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import java.util.function.Consumer;

public interface NonVanillaScreen {

    interface Factory {

        NonVanillaScreen create(FactoryParams params);
    }

    class FactoryParams {

        private final DatabaseGateway database;

        private final EntityController entityController;

        private final CallConnectionController callConnectionController;

        private final CallStateController callStateController;

        private final CallRequestController callRequestController;

        private final CallResponseController callResponseController;

        private final Consumer<EntityPresenter> entityPresenterRegistry;
        private final Consumer<CallConnectionPresenter> callConnectionPresenterRegistry;
        private final Consumer<CallResponsePresenter> callResponsePresenterRegistry;
        private final Consumer<CallRequestPresenter> callRequestPresenterRegistry;

        public FactoryParams(
                DatabaseGateway database,
                EntityController entityController,
                CallConnectionController callConnectionController,
                CallStateController callStateController,
                CallRequestController callRequestController,
                CallResponseController callResponseController,
                Consumer<EntityPresenter> entityPresenterRegistry,
                Consumer<CallConnectionPresenter> callConnectionPresenterRegistry,
                Consumer<CallResponsePresenter> callResponsePresenterRegistry,
                Consumer<CallRequestPresenter> callRequestPresenterRegistry) {
            this.database = database;
            this.entityController = entityController;
            this.callConnectionController = callConnectionController;
            this.callStateController = callStateController;
            this.callRequestController = callRequestController;
            this.callResponseController = callResponseController;
            this.entityPresenterRegistry = entityPresenterRegistry;
            this.callConnectionPresenterRegistry = callConnectionPresenterRegistry;
            this.callResponsePresenterRegistry = callResponsePresenterRegistry;
            this.callRequestPresenterRegistry = callRequestPresenterRegistry;
        }

        public Consumer<CallRequestPresenter> getCallRequestPresenterRegistry() {
            return callRequestPresenterRegistry;
        }

        public Consumer<CallResponsePresenter> getCallResponsePresenterRegistry() {
            return callResponsePresenterRegistry;
        }

        public Consumer<EntityPresenter> getEntityPresenterRegistry() {
            return entityPresenterRegistry;
        }

        public Consumer<CallConnectionPresenter> getCallConnectionPresenterRegistry() {
            return callConnectionPresenterRegistry;
        }

        public DatabaseGateway getDatabase() {
            return database;
        }

        public EntityController getEntityController() {
            return entityController;
        }

        public CallConnectionController getCallConnectionController() {
            return callConnectionController;
        }

        public CallStateController getCallStateController() {
            return callStateController;
        }

        public CallRequestController getCallRequestController() {
            return callRequestController;
        }

        public CallResponseController getCallResponseController() {
            return callResponseController;
        }
    }
}
