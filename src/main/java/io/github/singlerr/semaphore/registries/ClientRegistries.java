/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.registries;

import com.mojang.authlib.GameProfile;
import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.eventhandler.ItemInteractionHandler;
import io.github.singlerr.semaphore.eventhandler.NotificationRenderer;
import io.github.singlerr.semaphore.gui.NotificationWindow;
import io.github.singlerr.semaphore.gui.PhoneScreen;
import io.github.singlerr.semaphore.sound.utils.AudioPlayer;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.utils.EventPool;
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

@Log4j2
@NoArgsConstructor(access = AccessLevel.NONE)
public final class ClientRegistries {

    public static final KeyBinding KEY_SHOW_PHONE =
            new KeyBinding("key.phone.show", Keyboard.KEY_G, "key.semaphore.category");

    public static final KeyBinding KEY_ACCEPT_CALL =
            new KeyBinding("key.call.accept", Keyboard.KEY_X, "key.semaphore.category");
    public static final KeyBinding KEY_DENY_CALL =
            new KeyBinding("key.call.deny", Keyboard.KEY_C, "key.semaphore.category");

    public static final SoundEvent SOUND_CALL_NO = new SoundEvent(new ResourceLocation(Semaphore.MOD_ID, "call_no"));
    public static final SoundEvent SOUND_CALL_YES = new SoundEvent(new ResourceLocation(Semaphore.MOD_ID, "call_yes"));
    public static final SoundEvent SOUND_CALL_OFF = new SoundEvent(new ResourceLocation(Semaphore.MOD_ID, "call_off"));
    public static final SoundEvent SOUND_CALLING = new SoundEvent(new ResourceLocation(Semaphore.MOD_ID, "calling"));

    public static final SoundEvent SOUND_PHONE_BELL =
            new SoundEvent(new ResourceLocation(Semaphore.MOD_ID, "phone_bell"));
    public static final SoundEvent SOUND_PHONE_TOUCH =
            new SoundEvent(new ResourceLocation(Semaphore.MOD_ID, "phone_touch"));
    public static final SoundEvent SOUND_PHONE_VIBRATE =
            new SoundEvent(new ResourceLocation(Semaphore.MOD_ID, "phone_vibrate"));

    public static final ResourceLocationBuilder MISS_CALL_SOUND = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("sounds")
            .append("miss_call.wav");

    public static final ResourceLocationBuilder IN_CALL_SOUND = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("sounds")
            .append("in_call.wav");

    private static final Map<UUID, NotificationWindow> windowCaches = new ConcurrentHashMap<>();

    /***
     * Due to kotlin's severe error of lombok @Getter recognition
     * we unfortunately have to reveal the field to public
     */
    @Getter
    public static PlayerContext playerState;

    @Getter
    public static PhoneScreen phoneScreen;

    private static final EventPool eventPool = new EventPool();

    public static EventPool getEventPool() {
        return eventPool;
    }

    public static NotificationWindow getOrCreateNotificationWindow(UUID id) {
        NotificationWindow window;
        if (!windowCaches.containsKey(id)) {
            window = new NotificationWindow(id);
            windowCaches.put(id, window);
        } else {
            window = windowCaches.get(id);
        }

        return window;
    }

    public static void apply(FMLPreInitializationEvent event) {}

    public static void apply(FMLInitializationEvent event) {
        log.info("Registering key binds");

        ClientRegistry.registerKeyBinding(KEY_SHOW_PHONE);
        ClientRegistry.registerKeyBinding(KEY_ACCEPT_CALL);
        ClientRegistry.registerKeyBinding(KEY_DENY_CALL);

        log.info("Registering event handler");
        MinecraftForge.EVENT_BUS.register(new ItemInteractionHandler());
        MinecraftForge.EVENT_BUS.register(new NotificationRenderer());

        log.info("Initializing player state");
        GameProfile profile = Minecraft.getMinecraft().getSession().getProfile();
        playerState = PlayerContext.builder()
                .owner(profile.getId())
                .name(profile.getName())
                .build();
        log.info("Initialized player state {}", playerState);

        log.info("Initializing gui screen");
        phoneScreen = new PhoneScreen(playerState);
        try {
            AudioPlayer.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
