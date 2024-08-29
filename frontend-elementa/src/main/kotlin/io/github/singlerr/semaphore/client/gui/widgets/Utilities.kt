package io.github.singlerr.semaphore.client.gui.widgets

import gg.essential.universal.UMinecraft
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import net.minecraft.util.ResourceLocation

fun loadResource(resourceLocation: ResourceLocation, cut: Box? = null): BufferedImage {
    val image =
        ImageIO.read(
            UMinecraft.getMinecraft().resourceManager.getResource(resourceLocation).inputStream
        )
    return cut?.let { box -> image.getSubimage(box.x, box.y, box.width, box.height) } ?: image
}

data class Box(val x: Int, val y: Int, val width: Int, val height: Int)
