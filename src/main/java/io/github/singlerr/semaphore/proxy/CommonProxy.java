/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.proxy;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.instances.*;
import io.github.singlerr.semaphore.instances.common.CommonResources;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.AdminInteractor;
import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;
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
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class CommonProxy {

    protected CallConnectionPresenterAdapter callConnectionPresenter;
    protected EntityPresenterAdapter entityPresenter;

    protected CallRequestPresenterAdapter callRequestPresenter;
    protected ErrorPresenterAdapter errorPresenter;

    protected ErrorHandlerAdapter errorHandler;
    protected CallPresenterAdapter responsePresenter;

    public void preInit() {
        NetworkManager networkManager = new NetworkManager(Semaphore.MOD_ID);
        NetworkManagerAccess.setInstance(networkManager);
        initPolicy();
        initRemotePolicy(networkManager);
        MinecraftForge.EVENT_BUS.register(new BlockRegistries());
        MinecraftForge.EVENT_BUS.register(new ItemRegistries());
    }

    public void init() {}

    public void postInit() {}

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
        CallerInteractor callerInteractor = new SimpleCallerInteractor(database, errorPresenter, callRequestPresenter);

        // Lazy init
        errorHandler = new ErrorHandlerAdapter(ErrorHandlerAdapter.ErrorContext::new);
        responsePresenter = new CallPresenterAdapter(CallPresenterAdapter.PresenterContext::new);

        CalleeInteractor calleeInteractor =
                new SimpleCalleeInteractor(database, callConnectionHandler, errorHandler, responsePresenter);

        // Make Accessor store
        DatabaseAccess.setInstance(database);
        CallConnectionHandlerAccess.setInstance(callConnectionHandler);
        AdminInteractorAccess.setInstance(adminInteractor);
        CallerInteractorAccess.setInstance(callerInteractor);
        CalleeInteractorAccess.setInstance(calleeInteractor);
    }

    private static class BlockRegistries {

        public void registerBlock(RegistryEvent.Register<Block> registry) {}
    }

    private static class ItemRegistries {

        private ItemPhone phone = new ItemPhone();
        private ItemControlPanel controlPanel = new ItemControlPanel();

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

            registry.getRegistry().register(phone);
            registry.getRegistry().register(controlPanel);
            CommonResources.setInstance(ItemPhone.class, phone);
            CommonResources.setInstance(ItemControlPanel.class, controlPanel);
        }

        @SubscribeEvent
        public void registerItemModel(ModelRegistryEvent registry) {
            ModelLoader.setCustomModelResourceLocation(
                    phone, 0, new ModelResourceLocation(phone.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(
                    controlPanel, 0, new ModelResourceLocation(phone.getRegistryName(), "inventory"));
        }
    }
}
