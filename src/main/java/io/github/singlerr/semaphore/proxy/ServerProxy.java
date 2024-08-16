/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.proxy;

import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import io.github.singlerr.semaphore.compat.VoicechatCallConnectionHandler;
import io.github.singlerr.semaphore.instances.*;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.admin.AdminInteractor;
import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.packet.*;
import io.github.singlerr.semaphore.network.admin.server.*;
import io.github.singlerr.semaphore.network.admin.server.handler.CallConnectionHandlers;
import io.github.singlerr.semaphore.network.admin.server.handler.CallStateHandlers;
import io.github.singlerr.semaphore.network.admin.server.handler.EntityHandlers;
import io.github.singlerr.semaphore.network.callee.packet.PacketCallResponse;
import io.github.singlerr.semaphore.network.callee.server.ServerboundCallResponseController;
import io.github.singlerr.semaphore.network.callee.server.ServerboundCallResponsePresenter;
import io.github.singlerr.semaphore.network.callee.server.handler.CallResponseHandlers;
import io.github.singlerr.semaphore.network.caller.packet.PacketCallRequest;
import io.github.singlerr.semaphore.network.caller.packet.PacketInverseCallRequest;
import io.github.singlerr.semaphore.network.caller.server.ServerboundCallRequestController;
import io.github.singlerr.semaphore.network.caller.server.ServerboundCallRequestPresenter;
import io.github.singlerr.semaphore.network.caller.server.handlers.CallRequestHandlers;
import io.github.singlerr.semaphore.policy.admin.controllers.CallConnectionControllerAdapter;
import io.github.singlerr.semaphore.policy.admin.controllers.CallStateControllerAdapter;
import io.github.singlerr.semaphore.policy.admin.controllers.EntityControllerAdapter;
import io.github.singlerr.semaphore.policy.admin.presenters.CallConnectionPresenterAdapter;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.controller.RemoteCallResponseController;
import io.github.singlerr.semaphore.policy.callee.presenters.CallPresenterAdapter;
import io.github.singlerr.semaphore.policy.caller.controllers.RemoteCallRequestController;
import io.github.singlerr.semaphore.policy.caller.presenters.CallRequestPresenterAdapter;
import io.github.singlerr.semaphore.policy.callhandler.CallConnectionHandlerAdapter;

public final class ServerProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    @Override
    protected void initRemotePolicy(NetworkManager networkManager) {
        initAdmin(networkManager);
        initCallee(networkManager);
        initCaller(networkManager);
    }

    private void initAdmin(NetworkManager networkManager) {
        AdminInteractor adminInteractor = AdminInteractorAccess.getInstance();

        // Use ctx to send packet to exact player on presenter
        callConnectionPresenter.initialize(new CallConnectionPresenterAdapter.PredicatePresenter(
                (ctx) -> true, (ctx) -> true, new ServerboundCallConnectionPresenter(networkManager)));
        entityPresenter.initialize(new EntityPresenterAdapter.PredicatePresenter(
                (ctx) -> true, (ctx) -> true, new ServerboundEntityPresenter(networkManager)));

        ServerboundCallConnectionController connectionController = new ServerboundCallConnectionController(
                new CallConnectionControllerAdapter(adminInteractor.getConnectionManager(), callConnectionPresenter));
        ServerboundCallStateController stateController = new ServerboundCallStateController(
                new CallStateControllerAdapter(adminInteractor.getStateManager(), entityPresenter));
        ServerboundEntityController entityController = new ServerboundEntityController(
                new EntityControllerAdapter(DatabaseAccess.getInstance(), entityPresenter));
        // Presenter
        networkManager.registerServerboundPacket(PacketPresentableCallConnection.class);
        networkManager.registerServerboundPacket(PacketErrorEntity.class);
        // Controller
        networkManager.registerServerboundPacket(
                PacketOpenConnection.class, new CallConnectionHandlers.OpenConnectionHandler(connectionController));
        networkManager.registerServerboundPacket(
                PacketCloseConnection.class, new CallConnectionHandlers.CloseConnectionHandler(connectionController));
        networkManager.registerServerboundPacket(
                PacketGetConnection.class, new CallConnectionHandlers.GetConnectionHandler(connectionController));

        networkManager.registerServerboundPacket(
                PacketSetCallState.class, new CallStateHandlers.SetCallStateHandler(stateController));
        networkManager.registerServerboundPacket(
                PacketGetCallState.class, new CallStateHandlers.GetCallStateHandler(stateController));

        networkManager.registerServerboundPacket(
                PacketCreateEntity.class, new EntityHandlers.CreateEntityHandler(entityController));
        networkManager.registerServerboundPacket(
                PacketDeleteEntity.class, new EntityHandlers.DeleteEntityHandler(entityController));
        networkManager.registerServerboundPacket(
                PacketGetEntity.class, new EntityHandlers.GetEntityHandler(entityController));
        networkManager.registerServerboundPacket(PacketGetAllEntities.class, new EntityHandlers.GetAllEntitiesHandler(entityController));
        networkManager.registerServerboundPacket(PacketPresentableEntity.class);
        networkManager.registerServerboundPacket(PacketPresentableEntities.class);
    }

    // Register call response
    private void initCallee(NetworkManager networkManager) {
        CalleeInteractor calleeInteractor = CalleeInteractorAccess.getInstance();
        ServerboundCallResponseController responseController = new ServerboundCallResponseController(
                new RemoteCallResponseController(calleeInteractor.getResponseManager()));

        networkManager.registerServerboundPacket(
                PacketCallResponse.class, new CallResponseHandlers.CallResponseHandler(responseController));
        responsePresenter.initialize(new CallPresenterAdapter.PredicatePresenter(
                (ctx) -> true, new ServerboundCallResponsePresenter(networkManager)));
    }

    // Register call request
    private void initCaller(NetworkManager networkManager) {
        CallerInteractor callerInteractor = CallerInteractorAccess.getInstance();

        ServerboundCallRequestController requestController = new ServerboundCallRequestController(
                new RemoteCallRequestController(callerInteractor.getCallRequestManager()));
        networkManager.registerServerboundPacket(
                PacketCallRequest.class, new CallRequestHandlers.CallRequestHandler(requestController));
        networkManager.registerServerboundPacket(PacketInverseCallRequest.class);

        callRequestPresenter.initialize(new CallRequestPresenterAdapter.PredicatePresenter(
                (ctx) -> true, new ServerboundCallRequestPresenter(networkManager)));
    }

    public void serverStarted(VoicechatServerStartedEvent event) {
        // On server side, call connection handler hooks Voicechat api so that it can get full control of call
        // connection
        // On the other hand, on client side, call connection handler is just stub, which has no operations
        CallConnectionHandler voicechatBasedHandler =
                new VoicechatCallConnectionHandler(DatabaseAccess.getInstance(), event.getVoicechat());
        if (CallConnectionHandlerAccess.getInstance() instanceof CallConnectionHandlerAdapter) {
            ((CallConnectionHandlerAdapter) CallConnectionHandlerAccess.getInstance())
                    .setAdapter(voicechatBasedHandler);
        }
    }
}
