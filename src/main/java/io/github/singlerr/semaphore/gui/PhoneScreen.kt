package io.github.singlerr.semaphore.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.utils.ResourceCache
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.imageio.ImageIO
import javax.swing.WindowConstants

class PhoneScreen : WindowScreen(ElementaVersion.V5) {

    companion object{
        private val cache:ConcurrentMap<ResourceLocation,BufferedImage> = ConcurrentHashMap()
    }

    private val phoneFrame: ResourceLocation = ResourceLocationBuilder.builder()
        .namespace(Semaphore.MOD_ID)
        .append("textures")
        .append("gui")
        .append("phone_frame.png")
        .build()



    init{

        val frame = UIImage(if(cache.containsKey(phoneFrame)){ CompletableFuture.completedFuture(cache[phoneFrame]) } else { CompletableFuture.supplyAsync {
            val image = ImageIO.read(Minecraft.getMinecraft().resourceManager.getResource(phoneFrame).inputStream)
            cache[phoneFrame] = image
            return@supplyAsync image
        }
        }).constrain {
            x = 0.pixels()
            y = 5.percent()
            width = 40.percent()
            height = 80.percent()
        } childOf window

        val scroll = ScrollComponent(innerPadding = 2f, scrollDirection = ScrollComponent.Direction.Vertical).constrain {
            x = 100.percent() boundTo frame
            y = 10.percent() boundTo frame

            width = 90.percent() boundTo frame
            height = 100.percent() boundTo frame
        } childOf frame

        repeat(5){
            UIBlock(Color.RED).constrain {
                x = 5.pixels()
                y = 5.pixels()
                width = 100.percent() boundTo scroll
                height = 16.pixels() boundTo scroll
            } childOf scroll
        }


    }

    override fun drawDefaultBackground() {

    }

    override fun drawBackground(tint: Int) {

    }
}