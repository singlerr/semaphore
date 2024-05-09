/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.regisries;

import com.mojang.authlib.GameProfile;
import io.github.singlerr.semaphore.eventhandler.ItemInteractionHandler;
import io.github.singlerr.semaphore.eventhandler.PhoneRenderer;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ClientRegistries {

    public static final KeyBinding KEY_SHOW_PHONE =
            new KeyBinding("key.phone.show", Keyboard.KEY_G, "key.semaphore.category");

    public static void apply(FMLPreInitializationEvent event) {}

    public static void apply(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(KEY_SHOW_PHONE);
        MinecraftForge.EVENT_BUS.register(new ItemInteractionHandler());
        MinecraftForge.EVENT_BUS.register(new PhoneRenderer());
        initializeLocalPlayerContext();
    }

    private static void initializeLocalPlayerContext() {
        GameProfile profile = Minecraft.getMinecraft().getSession().getProfile();
        UUID userId = profile.getId();
        String name = profile.getName();
        CommonRegistries.getStatePool().submit(userId, PlayerContext.builder().owner(userId).name(name).build());
    }

    public static void apply(FMLPostInitializationEvent event) {}
}
