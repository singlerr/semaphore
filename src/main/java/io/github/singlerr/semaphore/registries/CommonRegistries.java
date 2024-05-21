/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.registries;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.item.ItemPhone;
import io.github.singlerr.semaphore.network.packets.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@NoArgsConstructor(access = AccessLevel.MODULE)
public final class CommonRegistries {

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Semaphore.MOD_ID);

    public static final ItemPhone ITEM_PHONE = new ItemPhone();

    public static final CreativeTabs ITEM_PHONE_TAB = new CreativeTabs(Semaphore.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ITEM_PHONE);
        }
    };

    public static void apply(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventBasedRegistry());
    }

    public static void apply(FMLInitializationEvent event) {
        initializeNetwork();
    }

    public static void apply(FMLPostInitializationEvent event) {}

    private static void initializeNetwork() {
        int index = 0;

        NETWORK.registerMessage(AddPlayerStatePacket.Handler.class, AddPlayerStatePacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(AddPlayerStatePacket.Handler.class, AddPlayerStatePacket.class, index++, Side.SERVER);

        NETWORK.registerMessage(CallAcceptPacket.Handler.class, CallAcceptPacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(CallAcceptPacket.Handler.class, CallAcceptPacket.class, index++, Side.SERVER);

        NETWORK.registerMessage(CallEstablishedPacket.Handler.class, CallEstablishedPacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(CallEstablishedPacket.Handler.class, CallEstablishedPacket.class, index++, Side.SERVER);

        NETWORK.registerMessage(CallRejectPacket.Handler.class, CallRejectPacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(CallRejectPacket.Handler.class, CallRejectPacket.class, index++, Side.SERVER);

        NETWORK.registerMessage(CallClosePacket.Handler.class, CallClosePacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(CallClosePacket.Handler.class, CallClosePacket.class, index++, Side.SERVER);

        NETWORK.registerMessage(
                InitializePlayerStatePacket.Handler.class, InitializePlayerStatePacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(
                InitializePlayerStatePacket.Handler.class, InitializePlayerStatePacket.class, index++, Side.SERVER);

        NETWORK.registerMessage(
                RemovePlayerStatePacket.Handler.class, RemovePlayerStatePacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(
                RemovePlayerStatePacket.Handler.class, RemovePlayerStatePacket.class, index++, Side.SERVER);

        NETWORK.registerMessage(CallRequestPacket.Handler.class, CallRequestPacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(CallRequestPacket.Handler.class, CallRequestPacket.class, index++, Side.SERVER);

        NETWORK.registerMessage(
                UpdatePlayerStatePacket.Handler.class, UpdatePlayerStatePacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(
                UpdatePlayerStatePacket.Handler.class, UpdatePlayerStatePacket.class, index++, Side.SERVER);

        NETWORK.registerMessage(CallMissedPacket.Handler.class, CallMissedPacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(CallMissedPacket.Handler.class, CallMissedPacket.class, index++, Side.SERVER);
    }

    private static class EventBasedRegistry {

        @SubscribeEvent
        public void onItemRegister(RegistryEvent.Register<Item> registry) {

            ITEM_PHONE.setCreativeTab(ITEM_PHONE_TAB);
            registry.getRegistry().register(ITEM_PHONE);
        }

        @SubscribeEvent
        public void onModelRegister(ModelRegistryEvent event) {
            ModelLoader.setCustomModelResourceLocation(
                    ITEM_PHONE, 0, new ModelResourceLocation(ITEM_PHONE.getRegistryName(), "inventory"));
        }
    }
}
