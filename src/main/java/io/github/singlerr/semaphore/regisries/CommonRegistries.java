/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.regisries;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.item.ItemPhone;
import io.github.singlerr.semaphore.network.packets.PlayerStatePacket;
import io.github.singlerr.semaphore.state.StatePool;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.EventPool;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
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

    @Getter
    private static final StatePool statePool = new StatePool();

    @Getter
    private static final EventPool eventPool = new EventPool();

    public static void apply(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventBasedRegistry());
    }

    public static void apply(FMLInitializationEvent event) {
        initializeNetwork();

        for (int i = 0; i < 10; i++) {
            UUID id = UUID.randomUUID();
            statePool.submit(
                    id,
                    PlayerContext.builder()
                            .name(id.toString().substring(0, 5))
                            .owner(id)
                            .build());
        }

        eventPool.subscribe(PlayerStatePacket.class, CommonRegistries::updatePlayerState);
    }

    public static void apply(FMLPostInitializationEvent event) {}

    private static void initializeNetwork() {
        int index = 0;
        NETWORK.registerMessage(PlayerStatePacket.Handler.class, PlayerStatePacket.class, index++, Side.CLIENT);
        NETWORK.registerMessage(PlayerStatePacket.Handler.class, PlayerStatePacket.class, index++, Side.SERVER);
    }

    public static void updatePlayerState(PlayerStatePacket packet) {
        statePool.submit(packet.getId(), packet.getState());
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
