/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.core;

import io.github.singlerr.semaphore.state.player.LogicalPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin implements LogicalPlayer {

    @Shadow
    public abstract String getName();

    @Override
    public String getPlayerName() {
        return getName();
    }
}
