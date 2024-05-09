/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.compat;

import gg.essential.elementa.components.UIImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UIImage.TextureScalingMode.class)
public interface TextureScalingModeAccessor {

    @Accessor("glMode")
    int getGlMode();
}
