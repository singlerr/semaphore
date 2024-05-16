/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import io.github.singlerr.semaphore.Semaphore;
import lombok.experimental.UtilityClass;

@UtilityClass
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
}
