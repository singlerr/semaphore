/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.proxy;

import io.github.singlerr.semaphore.client.gui.GuiControlPanel;
import io.github.singlerr.semaphore.client.gui.GuiPhone;
import io.github.singlerr.semaphore.client.listener.ItemEventListener;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.client.*;
import io.github.singlerr.semaphore.network.admin.client.handler.CallConnectionHandlers;
import io.github.singlerr.semaphore.network.admin.client.handler.EntityHandlers;
import io.github.singlerr.semaphore.network.admin.packet.*;
import io.github.singlerr.semaphore.network.callee.client.ClientboundCallResponseController;
import io.github.singlerr.semaphore.network.callee.packet.PacketCallResponse;
import io.github.singlerr.semaphore.network.caller.client.ClientboundCallRequestController;
import io.github.singlerr.semaphore.network.caller.client.ClientboundCallRequestPresenter;
import io.github.singlerr.semaphore.network.caller.client.handlers.CallRequestHandlers;
import io.github.singlerr.semaphore.network.caller.packet.PacketCallRequest;
import io.github.singlerr.semaphore.network.caller.packet.PacketInverseCallRequest;
import io.github.singlerr.semaphore.policy.admin.presenters.CallConnectionPresenterAdapter;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.presenters.CallPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.presenters.ErrorHandlerAdapter;
import io.github.singlerr.semaphore.policy.caller.presenters.CallRequestPresenterAdapter;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;

public final class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(new ItemEventListener());
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
        List<EntityPresenterAdapter.PredicatePresenter> entityPresenters = new ArrayList<>();
        List<CallConnectionPresenterAdapter.PredicatePresenter> connectionPresenters = new ArrayList<>();
        List<CallPresenterAdapter.PredicatePresenter> responsePresenters = new ArrayList<>();
        List<CallRequestPresenterAdapter.PredicatePresenter> requestPresenters = new ArrayList<>();
        List<ErrorHandlerAdapter.PredicatePresenter> errorPresenters = new ArrayList<>();

        initAdmin(networkManager, entityPresenters, connectionPresenters);
        initCalleeAndCaller(networkManager, responsePresenters, requestPresenters, errorPresenters);

        entityPresenter.initialize(entityPresenters.toArray(new EntityPresenterAdapter.PredicatePresenter[0]));
        callConnectionPresenter.initialize(
                connectionPresenters.toArray(new CallConnectionPresenterAdapter.PredicatePresenter[0]));
        callRequestPresenter.initialize(
                requestPresenters.toArray(new CallRequestPresenterAdapter.PredicatePresenter[0]));
        responsePresenter.initialize(responsePresenters.toArray(new CallPresenterAdapter.PredicatePresenter[0]));
    }

    private void initAdmin(
            NetworkManager networkManager,
            List<EntityPresenterAdapter.PredicatePresenter> entityPresenters,
            List<CallConnectionPresenterAdapter.PredicatePresenter> connectionPresenters) {
        ClientboundCallConnectionController connectionController =
                new ClientboundCallConnectionController(networkManager);
        ClientboundCallStateController stateController = new ClientboundCallStateController(networkManager);
        ClientboundEntityController entityController = new ClientboundEntityController(networkManager);

        // Init gui based presenter & controller
        GuiControlPanel guiControlPanel = new GuiControlPanel(entityController, connectionController, stateController);
        ClientResources.setInstance(GuiControlPanel.class, guiControlPanel);
        // Presenter
        entityPresenters.add(new EntityPresenterAdapter.PredicatePresenter(
                guiControlPanel::shouldPresent, guiControlPanel::shouldPresent, guiControlPanel));
        connectionPresenters.add(new CallConnectionPresenterAdapter.PredicatePresenter(
                guiControlPanel::shouldPresent, guiControlPanel::shouldPresent, guiControlPanel));

        // Init gui based presenter
        ClientboundCallConnectionPresenter presenter = new ClientboundCallConnectionPresenter(callConnectionPresenter);
        ClientboundEntityPresenter clientEntityPresenter = new ClientboundEntityPresenter(entityPresenter);

        networkManager.registerClientboundPacket(
                PacketPresentableCallConnection.class,
                new CallConnectionHandlers.PresentableCallConnectionHandler(presenter));
        networkManager.registerClientboundPacket(
                PacketErrorEntity.class, new CallConnectionHandlers.ErrorEntityHandler(presenter));
        // Controller
        // On client, all packets below are one-way
        networkManager.registerClientboundPacket(PacketOpenConnection.class);
        networkManager.registerClientboundPacket(PacketCloseConnection.class);
        networkManager.registerClientboundPacket(PacketGetConnection.class);

        networkManager.registerClientboundPacket(PacketSetCallState.class);
        networkManager.registerClientboundPacket(PacketGetCallState.class);

        networkManager.registerClientboundPacket(PacketCreateEntity.class);
        networkManager.registerClientboundPacket(PacketDeleteEntity.class);
        networkManager.registerClientboundPacket(PacketGetEntity.class);
        networkManager.registerClientboundPacket(PacketGetAllEntities.class);
        networkManager.registerClientboundPacket(
                PacketPresentableEntity.class, new EntityHandlers.PresentableEntityHandler(clientEntityPresenter));
        networkManager.registerClientboundPacket(
                PacketPresentableEntities.class, new EntityHandlers.PresentableEntitiesHandler(clientEntityPresenter));
    }

    private void initCalleeAndCaller(
            NetworkManager networkManager,
            List<CallPresenterAdapter.PredicatePresenter> responsePresenters,
            List<CallRequestPresenterAdapter.PredicatePresenter> requestPresenters,
            List<ErrorHandlerAdapter.PredicatePresenter> errorPresenters) {
        ClientboundCallRequestController callRequestController = new ClientboundCallRequestController(networkManager);
        ClientboundCallResponseController callResponseController =
                new ClientboundCallResponseController(networkManager);
        ClientboundCallRequestPresenter requestPresenter = new ClientboundCallRequestPresenter(callRequestPresenter);

        GuiPhone guiPhone = new GuiPhone(callRequestController, callResponseController);
        ClientResources.setInstance(GuiPhone.class, guiPhone);

        responsePresenters.add(new CallPresenterAdapter.PredicatePresenter(guiPhone::shouldPresent, guiPhone));
        errorPresenters.add(new ErrorHandlerAdapter.PredicatePresenter(guiPhone::shouldPresent, guiPhone));
        requestPresenters.add(new CallRequestPresenterAdapter.PredicatePresenter(guiPhone::shouldPresent, guiPhone));

        // User is both callee and caller, there's no need to split callee and caller
        // Must keep packet register order same with client and server
        networkManager.registerClientboundPacket(PacketCallResponse.class);
        networkManager.registerClientboundPacket(PacketCallRequest.class);
        networkManager.registerClientboundPacket(
                PacketInverseCallRequest.class, new CallRequestHandlers.InverseCallRequestHandler(requestPresenter));
    }
}
