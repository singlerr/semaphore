/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.utils;

import java.util.UUID;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;

@UtilityClass
public class ClientUtils {

    public UUID getClientUniqueId() {
        return Minecraft.getMinecraft().getSession().getProfile().getId();
    }
}
