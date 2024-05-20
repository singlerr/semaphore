/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.*
import java.awt.Color
import java.util.*

class UIVolume(private val ownerState: PlayerContext, val opponentId: UUID) :
    UIRoundedRectangle(radius = 5f) {

    private var percentage = BasicState("0%")

    init {
        constrain {
            x = CenterConstraint()
            y = SiblingConstraint() + 10.pixels()
            width = RelativeConstraint(1f)
            height = AspectConstraint(1 / 5f)
            color = primaryBackground().toConstraint()
        }

        val skin = GameProfileUtils.getSkin(opponentId)

        val headImage =
            UIPlayerSkull(skin).constrain {
                x = 5.pixels()
                y = CenterConstraint()
                width = RelativeConstraint(1 / 7f)
                height = ImageAspectConstraint()
            } childOf this

        val volumeIcon =
            UIImage(VOLUME_ICON.build().asImageAsync()).constrain {
                x = SiblingConstraint(1f) boundTo headImage
                y = CenterConstraint()
                width = RelativeConstraint(1 / 7f)
                height = AspectConstraint()
            } childOf this
        val percentageText =
            UIText().bindText(percentage).constrain {
                x = 5.pixels(alignOpposite = true)
                y = CenterConstraint()
            } childOf this
        val volume =
            UISlider(
                    this,
                    100.percent(),
                    max = 100f,
                    min = 0f,
                    defaultValue = ownerState.volumes[opponentId] ?: 100f,
                    barColor = Color.white,
                    valueConsumer = this::setPercentage
                )
                .constrain {
                    x = SiblingConstraint(2f) boundTo volumeIcon
                    y = CenterConstraint()
                    width = 31.pixels()
                    height = 10.pixels()
                } childOf this
    }

    private fun setPercentage(percentage: Float, value: Float) {
        this.percentage.set("${(percentage * 100).toInt()}%")
        ownerState.volumes[opponentId] = value
    }

    companion object {
        private val VOLUME_ICON: ResourceLocationBuilder =
            ResourceLocationBuilder.builder()
                .namespace(Semaphore.MOD_ID)
                .append("textures")
                .append("gui")
                .append("volume.png")
    }
}
