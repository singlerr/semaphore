/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.components.UIImage
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

class UIPlayerSkull(private val textureLocation: ResourceLocation) :
    UIImage(
        (CompletableFuture.supplyAsync {
            val image =
                ImageIO.read(
                    Minecraft.getMinecraft()
                        .resourceManager
                        .getResource(textureLocation)
                        .inputStream)
            image.getSubimage(8, 8, 8, 8)
        })) {}
