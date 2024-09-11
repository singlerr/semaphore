/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.proxy;

import io.github.singlerr.access.semaphore.client.gui.NonVanillaScreen;
import io.github.singlerr.semaphore.client.ClientWorldAwareInverseCallPresenter;
import io.github.singlerr.semaphore.client.gui.GuiControlPanel;
import io.github.singlerr.semaphore.client.gui.GuiPhone;
import io.github.singlerr.semaphore.client.listener.GuiEventListener;
import io.github.singlerr.semaphore.client.listener.ItemEventListener;
import io.github.singlerr.semaphore.client.sound.InteractionSoundHandler;
import io.github.singlerr.semaphore.client.sound.SoundPlayerImpl;
import io.github.singlerr.semaphore.client.sounds.SoundPlayerAccess;
import io.github.singlerr.semaphore.instances.DatabaseAccess;
import io.github.singlerr.semaphore.instances.client.ClientResources;
import io.github.singlerr.semaphore.interactors.admin.controller.CallConnectionController;
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.network.admin.client.*;
import io.github.singlerr.semaphore.network.admin.client.handler.CallConnectionHandlers;
import io.github.singlerr.semaphore.network.admin.client.handler.EntityHandlers;
import io.github.singlerr.semaphore.network.admin.packet.*;
import io.github.singlerr.semaphore.network.callee.client.ClientboundCallResponseController;
import io.github.singlerr.semaphore.network.callee.packet.PacketCallResponse;
import io.github.singlerr.semaphore.network.caller.client.ClientboundCallRequestController;
import io.github.singlerr.semaphore.network.caller.client.ClientboundCallRequestPresenter;
import io.github.singlerr.semaphore.network.caller.client.ClientboundCallResponsePresenter;
import io.github.singlerr.semaphore.network.caller.client.ClientboundErrorPresenter;
import io.github.singlerr.semaphore.network.caller.client.handlers.CallRequestHandlers;
import io.github.singlerr.semaphore.network.caller.client.handlers.CallResponseHandlers;
import io.github.singlerr.semaphore.network.caller.client.handlers.ErrorHandlers;
import io.github.singlerr.semaphore.network.caller.packet.PacketCallRequest;
import io.github.singlerr.semaphore.network.caller.packet.PacketError;
import io.github.singlerr.semaphore.network.caller.packet.PacketInverseCallRequest;
import io.github.singlerr.semaphore.policy.admin.presenters.CallConnectionPresenterAdapter;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.presenters.CallPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.presenters.ErrorHandlerAdapter;
import io.github.singlerr.semaphore.policy.caller.presenters.CallRequestPresenterAdapter;
import io.github.singlerr.semaphore.policy.caller.presenters.ErrorPresenterAdapter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public final class ClientProxy extends CommonProxy {

    private ClientboundCallRequestController requestController;
    private CallConnectionController connectionController;
    private CallStateController stateController;

    private final InteractionSoundHandler soundHandler = new InteractionSoundHandler();

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new GuiEventListener(entityController));
        MinecraftForge.EVENT_BUS.register(new ItemEventListener(entityController));
        SoundPlayerAccess.setInstance(
                new SoundPlayerImpl(Minecraft.getMinecraft().getSoundHandler()));
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
        connectionController = new ClientboundCallConnectionController(networkManager);
        stateController = new ClientboundCallStateController(networkManager);
        entityController = new ClientboundEntityController(networkManager);
        requestController = new ClientboundCallRequestController(networkManager);
        // Init gui based presenter & controller
        GuiControlPanel guiControlPanel =
                new GuiControlPanel(entityController, connectionController, stateController, requestController);
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

        networkManager.registerClientboundPacket(PacketOpenCall.class);
        networkManager.registerClientboundPacket(PacketCloseCall.class);
        networkManager.registerClientboundPacket(PacketCloseCallById.class);

        networkManager.registerClientboundPacket(PacketCreateEntity.class);
        networkManager.registerClientboundPacket(PacketDeleteEntity.class);
        networkManager.registerClientboundPacket(PacketGetEntity.class);
        networkManager.registerClientboundPacket(PacketGetAllEntities.class);
        networkManager.registerClientboundPacket(PacketUpdateEntity.class);

        networkManager.registerClientboundPacket(
                PacketPresentableEntity.class, new EntityHandlers.PresentableEntityHandler(clientEntityPresenter));
        networkManager.registerClientboundPacket(
                PacketPresentableEntities.class, new EntityHandlers.PresentableEntitiesHandler(clientEntityPresenter));
        networkManager.registerClientboundPacket(
                PacketEntityErrorEntity.class, new EntityHandlers.ErrorEntityHandler(clientEntityPresenter));
    }

    private void initCalleeAndCaller(
            NetworkManager networkManager,
            List<CallPresenterAdapter.PredicatePresenter> responsePresenters,
            List<CallRequestPresenterAdapter.PredicatePresenter> requestPresenters,
            List<ErrorHandlerAdapter.PredicatePresenter> errorPresenters) {

        ClientboundCallResponsePresenter clientboundCallResponsePresenter =
                new ClientboundCallResponsePresenter(responsePresenter);

        ClientboundCallResponseController callResponseController =
                new ClientboundCallResponseController(networkManager);
        ClientboundCallRequestPresenter requestPresenter = new ClientboundCallRequestPresenter(callRequestPresenter);
        ClientboundErrorPresenter clientboundErrorPresenter = new ClientboundErrorPresenter(errorPresenter);

        GuiPhone guiPhone = new GuiPhone(requestController, callResponseController);
        ClientResources.setInstance(GuiPhone.class, guiPhone);

        ClientWorldAwareInverseCallPresenter tileEntityNotifier = new ClientWorldAwareInverseCallPresenter();
        ClientResources.setInstance(ClientWorldAwareInverseCallPresenter.class, tileEntityNotifier);

        responsePresenters.add(new CallPresenterAdapter.PredicatePresenter(guiPhone::shouldPresent, guiPhone));
        errorPresenters.add(new ErrorHandlerAdapter.PredicatePresenter(guiPhone::shouldPresent, guiPhone));
        requestPresenters.add(new CallRequestPresenterAdapter.PredicatePresenter(guiPhone::shouldPresent, guiPhone));
        requestPresenters.add(new CallRequestPresenterAdapter.PredicatePresenter((ctx) -> true, tileEntityNotifier));
        requestPresenters.add(new CallRequestPresenterAdapter.PredicatePresenter((ctx) -> true, soundHandler));
        // User is both callee and caller, there's no need to split callee and caller
        // Must keep packet register order same with client and server
        networkManager.registerClientboundPacket(
                PacketCallResponse.class,
                new CallResponseHandlers.CallResponseHandler(clientboundCallResponsePresenter));
        networkManager.registerClientboundPacket(PacketCallRequest.class);
        networkManager.registerClientboundPacket(
                PacketInverseCallRequest.class, new CallRequestHandlers.InverseCallRequestHandler(requestPresenter));
        networkManager.registerClientboundPacket(
                PacketError.class, new ErrorHandlers.ErrorHandler(clientboundErrorPresenter));

        NonVanillaScreen.FactoryParams params = new NonVanillaScreen.FactoryParams(
                DatabaseAccess.getInstance(),
                entityController,
                connectionController,
                stateController,
                requestController,
                callResponseController,
                (p) -> entityPresenter.add(
                        new EntityPresenterAdapter.PredicatePresenter((ctx) -> true, (ctx) -> true, p)),
                (p) -> callConnectionPresenter.add(
                        new CallConnectionPresenterAdapter.PredicatePresenter((ctx) -> true, (ctx) -> true, p)),
                (p) -> responsePresenter.add(new CallPresenterAdapter.PredicatePresenter((ctx) -> true, p)),
                (p) -> callRequestPresenter.add(new CallRequestPresenterAdapter.PredicatePresenter((ctx) -> true, p)),
                (p) -> errorPresenter.add(new ErrorPresenterAdapter.PredicatePresenter((ctx) -> true, p)));
        ClientResources.setInstance(NonVanillaScreen.FactoryParams.class, params);
        ClientResources.setInstance(InteractionSoundHandler.class, soundHandler);
    }
}
