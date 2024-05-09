/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.gui.components.UIAddress.Companion
import io.github.singlerr.semaphore.utils.CompatibleSVGParser
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import io.github.singlerr.semaphore.utils.asInputStream
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

class UIVolume(playerId: UUID) : UIBlock() {

    private var percentage = BasicState("0%")

    init {
        constrain {
            x = CenterConstraint()
            y = SiblingConstraint() + 10.pixels()
            width = RelativeConstraint(1f)
            height = AspectConstraint(1 / 5f)
        }

        val skin = GameProfileUtils.getSkin(playerId)

        val headImage =
            UIPlayerSkull(skin).constrain {
                x = 5.pixels()
                y = CenterConstraint()
                width = RelativeConstraint(1 /7f)
                height = ImageAspectConstraint()
            } childOf this


        val volumeIcon = SVGComponent(CompatibleSVGParser.parse(VOLUME_ICON.build().asInputStream())).constrain {
            x = SiblingConstraint(1f) boundTo headImage
            y = CenterConstraint()
            width = RelativeConstraint(1 / 7f)
            height = AspectConstraint()
            color = Color.black.toConstraint()
        } childOf this
        val percentageText = UIText().bindText(percentage).constrain {
            x = 5.pixels(alignOpposite = true)
            y = CenterConstraint()
            width = RelativeConstraint(1 / 8f)
            height = AspectConstraint()
        } childOf this
        val volume =
            UISlider(this, 100.percent(), max = 100f, min = 0f, defaultValue = 100f, barColor = Color.cyan, valueConsumer = this::setPercentage)
                .constrain {
                    x = SiblingConstraint(1f) boundTo volumeIcon
                    y = CenterConstraint()
                    width = RelativeConstraint(3f) boundTo percentageText
                    height = 10.pixels()
                } childOf this


    }

    private fun setPercentage(percentage: Float, value: Float){
        this.percentage.set("${(percentage * 100).toInt()}%")
    }

    private fun getImage(resourceLocation: ResourceLocation): BufferedImage {
        return ImageIO.read(
            Minecraft.getMinecraft().resourceManager.getResource(resourceLocation).inputStream)
    }

    private fun getImageAsync(
        resourceLocation: ResourceLocation
    ): CompletableFuture<BufferedImage> {
        return CompletableFuture.supplyAsync { getImage(resourceLocation) }
    }

    companion object {
        private val VOLUME_ICON: ResourceLocationBuilder =
            ResourceLocationBuilder.builder()
                .namespace(Semaphore.MOD_ID)
                .append("textures")
                .append("gui")
                .append("volume.svg")
    }
}
