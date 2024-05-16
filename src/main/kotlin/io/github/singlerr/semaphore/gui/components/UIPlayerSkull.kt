/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.components.UIImage
import io.github.singlerr.semaphore.utils.asImageAsync
import net.minecraft.util.ResourceLocation

class UIPlayerSkull(textureLocation: ResourceLocation) :
    UIImage(textureLocation.asImageAsync { img -> img.getSubimage(8, 8, 8, 8) })
