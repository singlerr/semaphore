/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.regisries;

import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.item.ItemPhone;
import io.github.singlerr.semaphore.network.packets.PlayerStatePacket;
import io.github.singlerr.semaphore.state.StatePool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
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

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Semaphore.MOD_ID);

    public static final ItemPhone PHONE_ITEM = new ItemPhone();

    @Getter
    private static final StatePool statePool = new StatePool();

    public static void apply(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventBasedRegistry());
    }

    public static void apply(FMLInitializationEvent event) {
        initializeNetwork();
    }

    public static void apply(FMLPostInitializationEvent event) {}

    private static void initializeNetwork() {
        int index = 0;
        INSTANCE.registerMessage(PlayerStatePacket.Handler.class, PlayerStatePacket.class, index++, Side.CLIENT);
        INSTANCE.registerMessage(PlayerStatePacket.Handler.class, PlayerStatePacket.class, index++, Side.SERVER);
    }

    private static class EventBasedRegistry {

        @SubscribeEvent
        public void onItemRegister(RegistryEvent.Register<Item> registry) {
            registry.getRegistry().register(PHONE_ITEM);
        }
    }
}
