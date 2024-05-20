/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import io.github.singlerr.semaphore.Semaphore;
import net.minecraft.util.ResourceLocation;

public class Resources {
    public static final ResourceLocationBuilder ICON_CALL_ACCEPT = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("call_accept.png");
    public static final ResourceLocationBuilder ICON_CALL_DENY = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("call_deny.png");
    public static final ResourceLocationBuilder ICON_CALL_MISS = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("call_miss.png");
    public static final ResourceLocation SETTINGS_ICON = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("settings.png")
            .build();

    public static final ResourceLocation PHONE_FRAME = ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_bar.png")
            .build();
}
