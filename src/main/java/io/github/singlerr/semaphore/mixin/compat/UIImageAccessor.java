/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.mixin.compat;

import gg.essential.elementa.components.UIImage;
import gg.essential.universal.utils.ReleasedDynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UIImage.class)
public interface UIImageAccessor {

    @Accessor("texture")
    ReleasedDynamicTexture getTexture();
}
