/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.gui.components.UIAddressList
import io.github.singlerr.semaphore.gui.components.UISettings
import io.github.singlerr.semaphore.gui.components.UIVolumes
import io.github.singlerr.semaphore.regisries.CommonRegistries
import io.github.singlerr.semaphore.state.StatePool
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.imageio.ImageIO
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

class PhoneScreen(private val statePool: StatePool) : WindowScreen(ElementaVersion.V5) {

    companion object {
        private val cache: ConcurrentMap<ResourceLocation, BufferedImage> = ConcurrentHashMap()
    }

    private val phoneFrame: ResourceLocation =
        ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame.png")
            .build()

    private val frame: UIComponent

    var refresh: Boolean = false

    init {
        frame =
            UIImage(
                    if (cache.containsKey(phoneFrame)) {
                        CompletableFuture.completedFuture(cache[phoneFrame])
                    } else {
                        CompletableFuture.supplyAsync {
                            val image =
                                ImageIO.read(
                                    Minecraft.getMinecraft()
                                        .resourceManager
                                        .getResource(phoneFrame)
                                        .inputStream)
                            cache[phoneFrame] = image
                            return@supplyAsync image
                        }
                    })
                .constrain {
                    x = 0.pixels()
                    y = 5.percent()
                    width = 40.percent()
                    height = 80.percent()
                } childOf window

        val states =
            statePool
                .states
                .map { t -> t.value }
                .filterIsInstance<PlayerContext>()
//        val addressList = UIAddressList(frame, states)
        val volumes = UIVolumes(frame, states)

        Inspector(window).constrain {
            x = 10.pixels(true)
            y = 10.pixels(true)
        } childOf window
    }

    fun animate() {}
}
