/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.components.SVGComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.Semaphore
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

class UIAddress(playerId: UUID) : UIBlock() {

    init {
        setColor(Color.WHITE)
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
                width = RelativeConstraint(1 / 5f)
                height = ImageAspectConstraint()
            } childOf this
        val usernameText =
            UIWrappedText(playerId.toString(), shadow = false, trimText = true).constrain {
                x = SiblingConstraint(2f) boundTo headImage
                y = CenterConstraint()

                width = RelativeConstraint(1 / 8f)
                height = 10.pixels()
            } childOf this
        usernameText.setColor(Color.black)
        val callImage =
            SVGComponent(CompatibleSVGParser.parse(CALL_ICON.build().asInputStream())).constrain {
                x = 5.pixels(alignOpposite = true)
                y = CenterConstraint()
                width = RelativeConstraint(1 / 7f)
                height = AspectConstraint()
            } childOf this
        callImage.setColor(Color.green)

        val missCallImage =
            SVGComponent(CompatibleSVGParser.parse(CALL_ICON.build().asInputStream())).constrain {
                x = SiblingConstraint(2f, alignOpposite = true) boundTo callImage
                y = CenterConstraint()
                width = RelativeConstraint(1 / 7f)
                height = AspectConstraint()
            } childOf this
        missCallImage.setColor(Color.red)
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
        private val CALL_ICON: ResourceLocationBuilder =
            ResourceLocationBuilder.builder()
                .namespace(Semaphore.MOD_ID)
                .append("textures")
                .append("gui")
                .append("phone_call.svg")
    }
}
