package io.github.singlerr.semaphore.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import javax.swing.WindowConstants

class PhoneScreen : WindowScreen(ElementaVersion.V5) {
    private val phoneFrame: ResourceLocation = ResourceLocationBuilder.builder()
        .namespace(Semaphore.MOD_ID)
        .append("textures")
        .append("gui")
        .append("phone_frame.png")
        .build()

    init{


        val frame = UIImage(CompletableFuture.supplyAsync {
            ImageIO.read(Minecraft.getMinecraft().resourceManager.getResource(phoneFrame).inputStream)
        }).constrain {
            x = 5.pixels
            y = 5.pixels

            width = RelativeWindowConstraint(0.5f)
            height = RelativeWindowConstraint(0.5f)
        } childOf window

        val inner = UIBlock(Color.RED).constrain {
            x = 5.pixels
            y = 5.pixels

            width = RelativeWindowConstraint(0.3f)
            height = 10.pixels
        } childOf window

    }


}