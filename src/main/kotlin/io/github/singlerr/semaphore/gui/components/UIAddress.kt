/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import io.github.singlerr.semaphore.network.packets.CallRequestPacket
import io.github.singlerr.semaphore.registries.CommonRegistries
import io.github.singlerr.semaphore.sound.ClientSoundHandler
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.*
import java.awt.Color

class UIAddress(private var ownerState: PlayerContext, var currentState: PlayerContext) :
    UIRoundedRectangle(radius = 5f) {

    private val missCallState = BasicState(ownerState.missCalls.getOrDefault(currentState.owner, 0))
    private val missCallImage: UIComponent
    private val missCallCount: UIComponent

    init {
        setColor(primaryBackground())

        val darkerBackground = primaryBackground().darker()
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
        val buttonSize = RelativeConstraint(1 / 7f)
        val usernameText =
            UIWrappedText(currentState.name, shadow = false, trimText = true).constrain {
                x = SiblingConstraint(2f) boundTo headImage
                y = CenterConstraint()

                width = RelativeConstraint(2 / 8f)
                height = 10.pixels()

                color = Color.black.toConstraint()
            } childOf this

        val callImage =
            UIImage(Resources.ICON_CALL_ACCEPT.build().asImageAsync()).constrain {
                x = 5.pixels(alignOpposite = true)
                y = CenterConstraint()
                width = buttonSize
                height = AspectConstraint()
            } childOf this

        callImage.onMouseClick {
            // Call
            ownerState.callState = PlayerContext.CallState.CALLING
            ClientSoundHandler.playCallingSound()
            CommonRegistries.NETWORK.sendToServer(
                CallRequestPacket.builder().caller(ownerState).callee(currentState).build()
            )
        }.onMouseEnter {
            animate {
                setWidthAnimation(Animations.OUT_EXP, 0.3f, buttonSize * 1.1)
            }
        }.onMouseLeave {
            animate {
                setWidthAnimation(Animations.OUT_EXP, 0.3f, buttonSize)
            }
        }

        missCallImage =
            UIImage(Resources.ICON_CALL_MISS.build().asImageAsync()).constrain {
                x = SiblingConstraint(2f, alignOpposite = true) boundTo callImage
                y = CenterConstraint()
                width = RelativeConstraint(1 / 7f)
                height = AspectConstraint()
            } childOf this

        missCallCount =
            UIText().bindText(missCallState.map(Number::toString)).constrain {
                x = 1.pixels(true)
                y = 1.pixels()

                width = RelativeConstraint(1 / 5f)
                height = 5.pixels()
            } childOf missCallImage

        if (missCallState.get().toInt() <= 0) {
            missCallCount.hide(true)
            missCallImage.hide(true)
        }

        onMouseEnter {
            animate {
                setColorAnimation(Animations.OUT_EXP, 0.1f, darkerBackground.toConstraint())
            }
        }.onMouseLeave {
            animate {
                setColorAnimation(Animations.OUT_EXP, 0.1f, primaryBackground().toConstraint())
            }
        }
    }

    fun update(ownerState: PlayerContext, currentState: PlayerContext) {
        this.ownerState = ownerState
        this.currentState = currentState
        missCallState.set(ownerState.missCalls.getOrDefault(currentState.owner, 0))
        if (missCallState.get().toInt() > 0) {
            missCallCount.unhide()
            missCallImage.unhide()
        } else {
            missCallCount.hide(true)
            missCallImage.hide(true)
        }
    }
}
