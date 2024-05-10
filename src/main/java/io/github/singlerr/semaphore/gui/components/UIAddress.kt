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

class UIAddress(private var ownerState: PlayerContext, var currentState: PlayerContext) :
    UIRoundedRectangle(radius = 5f) {

    private val missCallState = BasicState(ownerState.missCalls.getOrDefault(currentState.owner, 0))

    init {
        setColor(primaryBackground())
        constrain {
            x = CenterConstraint()
            y = SiblingConstraint() + 10.pixels()
            width = RelativeConstraint(1f)
            height = AspectConstraint(1 / 5f)
        }

        val skin = GameProfileUtils.getSkin(currentState.owner)

        val headImage =
            UIPlayerSkull(skin).constrain {
                x = 5.pixels()
                y = CenterConstraint()
                width = RelativeConstraint(1 / 5f)
                height = ImageAspectConstraint()
            } childOf this

        val usernameText =
            UIWrappedText(currentState.name, shadow = false, trimText = true).constrain {
                x = SiblingConstraint(2f) boundTo headImage
                y = CenterConstraint()

                width = RelativeConstraint(1 / 8f)
                height = 10.pixels()

                color = Color.black.toConstraint()
            } childOf this

        val callImage =
            UIImage(CALL_ICON.build().asImageAsync()).constrain {
                x = 5.pixels(alignOpposite = true)
                y = CenterConstraint()
                width = RelativeConstraint(1 / 7f)
                height = AspectConstraint()
            } childOf this

        callImage.onMouseClick {
            // Call
        }

        val missCallImage =
            UIImage(CALL_ICON.build().asImageAsync()).constrain {
                x = SiblingConstraint(2f, alignOpposite = true) boundTo callImage
                y = CenterConstraint()
                width = RelativeConstraint(1 / 7f)
                height = AspectConstraint()
            } childOf this

        val missCallCount =
            UIText().bindText(missCallState.map(Number::toString)).constrain {
                x = 1.pixels(true)
                y = 1.pixels()

                width = RelativeConstraint(1 / 5f)
                height = 5.pixels()
            } childOf missCallImage
    }

    fun update(ownerState: PlayerContext, currentState: PlayerContext) {
        this.ownerState = ownerState
        this.currentState = currentState
        missCallState.set(ownerState.missCalls.getOrDefault(currentState.owner, 0))
    }

    companion object {
        private val CALL_ICON: ResourceLocationBuilder =
            ResourceLocationBuilder.builder()
                .namespace(Semaphore.MOD_ID)
                .append("textures")
                .append("gui")
                .append("phone_call.png")
    }
}
