/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.proxy;

import io.github.singlerr.semaphore.ModConfig;
import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.block.BlockPhoneBox;
import io.github.singlerr.semaphore.block.entity.TileEntityPhoneBox;
import io.github.singlerr.semaphore.client.ServerWorldAwareInverseCallPresenter;
import io.github.singlerr.semaphore.config.ConfigurationManager;
import io.github.singlerr.semaphore.instances.*;
import io.github.singlerr.semaphore.instances.common.CommonResources;
import io.github.singlerr.semaphore.instances.server.ServerResources;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.AdminInteractor;
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController;
import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;
import io.github.singlerr.semaphore.item.ItemBoxSelector;
import io.github.singlerr.semaphore.item.ItemControlPanel;
import io.github.singlerr.semaphore.item.ItemPhone;
import io.github.singlerr.semaphore.network.NetworkManager;
import io.github.singlerr.semaphore.policy.admin.SimpleAdminInteractor;
import io.github.singlerr.semaphore.policy.admin.presenters.CallConnectionPresenterAdapter;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.SimpleCalleeInteractor;
import io.github.singlerr.semaphore.policy.callee.presenters.CallPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.presenters.ErrorHandlerAdapter;
import io.github.singlerr.semaphore.policy.caller.SimpleCallerInteractor;
import io.github.singlerr.semaphore.policy.caller.presenters.CallRequestPresenterAdapter;
import io.github.singlerr.semaphore.policy.caller.presenters.ErrorPresenterAdapter;
import io.github.singlerr.semaphore.policy.callhandler.CallConnectionHandlerAdapter;
import io.github.singlerr.semaphore.policy.database.PlayerDatabase;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class CommonProxy {

    protected CallConnectionPresenterAdapter callConnectionPresenter;
    protected EntityPresenterAdapter entityPresenter;

    protected CallRequestPresenterAdapter callRequestPresenter;
    protected ErrorPresenterAdapter errorPresenter;

    protected ErrorHandlerAdapter errorHandler;
    protected CallPresenterAdapter responsePresenter;

    protected EntityController entityController;

    public void preInit() {
        NetworkManager networkManager = new NetworkManager(Semaphore.MOD_ID);
        NetworkManagerAccess.setInstance(networkManager);
        initPolicy();
        initRemotePolicy(networkManager);
        MinecraftForge.EVENT_BUS.register(new BlockRegistries());
        MinecraftForge.EVENT_BUS.register(new ItemRegistries(entityController));
    }

    public void init() {
        ConfigurationManager.getInstance().register(this::registerConfig, true);
    }

    public void postInit() {
        ConfigurationManager.getInstance().invokeRegistration();
    }

    private void registerConfig(ConfigurationManager.Registry registry) {
        registry.getConfig().getVolumes().observe((map) -> ModConfig.volumes = new HashMap<>(map));
    }

    protected void initRemotePolicy(NetworkManager networkManager) {}

    private void initPolicy() {
        DatabaseGateway database = new PlayerDatabase();

        // Due to different binding point of Minecraft and Voicechat, leave it to adapter
        CallConnectionHandler callConnectionHandler = new CallConnectionHandlerAdapter();

        // Lazy init
        // Register adapter and context supplier after all Minecraft components loaded
        callConnectionPresenter =
                new CallConnectionPresenterAdapter(CallConnectionPresenterAdapter.PresenterContext::new);
        entityPresenter = new EntityPresenterAdapter(EntityPresenterAdapter.PresenterContext::new);

        AdminInteractor adminInteractor =
                new SimpleAdminInteractor(database, callConnectionHandler, callConnectionPresenter, entityPresenter);

        // Lazy init
        callRequestPresenter = new CallRequestPresenterAdapter(CallRequestPresenterAdapter.PresenterContext::new);
        errorPresenter = new ErrorPresenterAdapter(ErrorPresenterAdapter.ErrorContext::new);
        CallerInteractor callerInteractor =
                new SimpleCallerInteractor(database, errorPresenter, callRequestPresenter, entityPresenter);

        // Lazy init
        errorHandler = new ErrorHandlerAdapter(ErrorHandlerAdapter.ErrorContext::new);
        responsePresenter = new CallPresenterAdapter(CallPresenterAdapter.PresenterContext::new);

        CalleeInteractor calleeInteractor = new SimpleCalleeInteractor(
                database, adminInteractor.getStateManager(), errorHandler, responsePresenter, entityPresenter);

        // Make Accessor store
        DatabaseAccess.setInstance(database);
        CallConnectionHandlerAccess.setInstance(callConnectionHandler);
        AdminInteractorAccess.setInstance(adminInteractor);
        CallerInteractorAccess.setInstance(callerInteractor);
        CalleeInteractorAccess.setInstance(calleeInteractor);
    }

    private static class BlockRegistries {

        private BlockPhoneBox phoneBox = new BlockPhoneBox();

        @SubscribeEvent
        public void registerBlock(RegistryEvent.Register<Block> registry) {
            registry.getRegistry().register(phoneBox);
            GameRegistry.registerTileEntity(
                    TileEntityPhoneBox.class, new ResourceLocation(Semaphore.MOD_ID, "phone_box"));
            CommonResources.setInstance(BlockPhoneBox.class, phoneBox);

            ServerWorldAwareInverseCallPresenter clientTileEntityNotifier =
                    ServerResources.getInstance(ServerWorldAwareInverseCallPresenter.class);
            if (clientTileEntityNotifier != null) phoneBox.setTracker(clientTileEntityNotifier);
        }
    }

    private static class ItemRegistries {

        private ItemPhone phone = new ItemPhone();
        private ItemControlPanel controlPanel = new ItemControlPanel();
        private ItemBoxSelector phoneBoxSelector;

        public ItemRegistries(EntityController entityController) {
            this.phoneBoxSelector = new ItemBoxSelector(entityController);
        }

        @SubscribeEvent
        public void registerItem(RegistryEvent.Register<Item> registry) {
            CreativeTabs tab = new CreativeTabs(Semaphore.MOD_ID) {
                @Override
                public ItemStack createIcon() {
                    return new ItemStack(phone);
                }
            };
            phone.setCreativeTab(tab);
            controlPanel.setCreativeTab(tab);
            phoneBoxSelector.setCreativeTab(tab);

            registry.getRegistry().register(phone);
            registry.getRegistry().register(controlPanel);
            registry.getRegistry().register(phoneBoxSelector);

            CommonResources.setInstance(ItemPhone.class, phone);
            CommonResources.setInstance(ItemControlPanel.class, controlPanel);
            CommonResources.setInstance(ItemBoxSelector.class, phoneBoxSelector);
        }

        @SubscribeEvent
        public void registerItemModel(ModelRegistryEvent registry) {
            ModelLoader.setCustomModelResourceLocation(
                    phone, 0, new ModelResourceLocation(phone.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(
                    controlPanel, 0, new ModelResourceLocation(controlPanel.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(
                    phoneBoxSelector, 0, new ModelResourceLocation(phoneBoxSelector.getRegistryName(), "inventory"));
        }
    }
}
