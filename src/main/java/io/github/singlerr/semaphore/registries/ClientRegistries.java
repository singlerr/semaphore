/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.registries;

import com.mojang.authlib.GameProfile;
import io.github.singlerr.semaphore.Semaphore;
import io.github.singlerr.semaphore.eventhandler.ItemInteractionHandler;
import io.github.singlerr.semaphore.eventhandler.NotificationRenderer;
import io.github.singlerr.semaphore.events.*;
import io.github.singlerr.semaphore.gui.NotificationWindow;
import io.github.singlerr.semaphore.gui.PhoneScreen;
import io.github.singlerr.semaphore.network.packets.CallActionPacket;
import io.github.singlerr.semaphore.network.packets.CallFeedbackPacket;
import io.github.singlerr.semaphore.network.packets.PlayerStatePacket;
import io.github.singlerr.semaphore.sound.AudioPlayer;
import io.github.singlerr.semaphore.sound.ClientSoundHandler;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import io.github.singlerr.semaphore.state.player.PlayerContextHandler;
import io.github.singlerr.semaphore.utils.EventPool;
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
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

    private static final Map<UUID, NotificationWindow> windowCaches = new ConcurrentHashMap<>();

    @Getter
    private static PhoneScreen phoneScreen;

    public static final ResourceLocationBuilder MISS_CALL_SOUND = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("sounds")
            .append("miss_call.wav");

    public static final ResourceLocationBuilder IN_CALL_SOUND = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("sounds")
            .append("in_call.wav");

    private static final EventPool eventPool = new EventPool();

    public static EventPool getEventPool() {
        return eventPool;
    }

    public static NotificationWindow getOrCreate(UUID id) {
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
        ClientRegistry.registerKeyBinding(KEY_SHOW_PHONE);
        ClientRegistry.registerKeyBinding(KEY_ACCEPT_CALL);
        ClientRegistry.registerKeyBinding(KEY_DENY_CALL);

        MinecraftForge.EVENT_BUS.register(new ItemInteractionHandler());
        MinecraftForge.EVENT_BUS.register(new NotificationRenderer());

        ClientSoundHandler.register(getEventPool());
        ServerSynchronizer.register(getEventPool());
        PlayerContextHandler.register(getEventPool());

        log.info("Initializing client context");
        initializeLocalPlayerContext();
        log.info("Initializing gui screen");
        phoneScreen = new PhoneScreen(
                CommonRegistries.getStatePool(),
                Minecraft.getMinecraft().getSession().getProfile().getId());
        phoneScreen.register(getEventPool());
        log.info("Registering event handler");
    }

    private static void initializeLocalPlayerContext() {
        GameProfile profile = Minecraft.getMinecraft().getSession().getProfile();
        UUID userId = profile.getId();
        String name = profile.getName();
        getEventPool()
                .invoke(new PlayerStateChangeEvent(
                        PlayerContext.builder().owner(userId).name(name).build()));
        //                #if DEV_MODE
        //                for (int i = 0; i < 5; i++) {
        //                    getEventPool()
        //                            .invoke(new PlayerStateChangeEvent(PlayerContext.builder()
        //                                    .owner(UUID.randomUUID())
        //                                    .name(UUID.randomUUID().toString())
        //                                    .build()));
        //                }
        //                #endif
    }

    public static void apply(FMLPostInitializationEvent event) {
        try {
            AudioPlayer.init();

            AudioPlayer.loadSound(MISS_CALL_SOUND);
            AudioPlayer.loadSound(IN_CALL_SOUND);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @UtilityClass
    private class ServerSynchronizer {

        public void register(EventPool eventPool) {
            eventPool.subscribe(SendingCallEvent.class, ServerSynchronizer::sendToServer);
            eventPool.subscribe(OutGoingCallFeedbackEvent.class, ServerSynchronizer::sendToServer);
            eventPool.subscribe(PlayerStateChangeEvent.class, ServerSynchronizer::sendToServer);
        }

        private void sendToServer(SendingCallEvent event) {
            CommonRegistries.NETWORK.sendToServer(CallActionPacket.builder()
                    .action(event.getAction())
                    .caller(event.getCaller())
                    .callee(event.getCallee())
                    .build());
        }

        private void sendToServer(OutGoingCallFeedbackEvent event) {
            CommonRegistries.NETWORK.sendToServer(CallFeedbackPacket.builder()
                    .callFeedback(event.getFeedback())
                    .callee(event.getCallee())
                    .caller(event.getCaller())
                    .build());
        }

        private void sendToServer(PlayerStateChangeEvent event) {
            // It handles only C2S event
            if (event.getSide() != Side.CLIENT) return;
            CommonRegistries.NETWORK.sendToServer(PlayerStatePacket.builder()
                    .state(event.getState())
                    .id(event.getState().getOwner())
                    .build());
        }
    }
}
