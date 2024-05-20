/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.network.packets.CallClosePacket
import io.github.singlerr.semaphore.registries.CommonRegistries
import io.github.singlerr.semaphore.sound.ClientSoundHandler
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.Resources
import io.github.singlerr.semaphore.utils.asImageAsync
import java.awt.Color
import java.net.URL
import java.util.UUID
import net.minecraft.client.resources.I18n

class UIOutComingCall(parent: UIComponent, ownerState: PlayerContext, calleeId: UUID) : UIBlock() {

    private val messageLabel: UIComponent

    init {
        constrain {
            x = 0.pixels() boundTo parent
            y = 0.pixels() boundTo parent

            width = 100.percent() boundTo parent
            height = 100.percent() boundTo parent
        }

        val skullImageLocation = GameProfileUtils.getSkin(calleeId)
        val playerSkull =
            UIImage.ofURL(URL("https://mc-heads.net/head/$calleeId")).constrain {
                x = CenterConstraint()
                y = 10.pixels() boundTo parent

                width = 30.percent() boundTo parent
                height = ImageAspectConstraint()
            } childOf this

        val btnSize = 20.pixels()
        val cancel =
            UIImage(Resources.ICON_CALL_DENY.build().asImageAsync()).constrain {
                x = CenterConstraint()
                y = 50.pixels(true)

                width = btnSize
                height = ImageAspectConstraint()
            } childOf this

        cancel.onMouseClick {
            ownerState.callState = PlayerContext.CallState.IDLE
            CommonRegistries.NETWORK.sendToServer(
                CallClosePacket.builder()
                    .caller(ownerState)
                    .callee(PlayerContext.from(calleeId))
                    .build()
            )
            ClientSoundHandler.playNoSound()
            this@UIOutComingCall.hide(true)
            parent.removeChild(this@UIOutComingCall)
        }

        messageLabel =
            UIWrappedText(I18n.format("gui.phonescreen.misscall")).constrain {
                x = CenterConstraint()
                y = 100.pixels(true)

                width = 100.percent() boundTo parent
            } childOf this
        cancel
            .onMouseEnter { animate { setWidthAnimation(Animations.OUT_EXP, 0.3f, btnSize * 1.1) } }
            .onMouseLeave { animate { setWidthAnimation(Animations.OUT_EXP, 0.3f, btnSize) } }
        messageLabel.hide(true)
    }

    fun callNotAvailable() {
        messageLabel.unhide()
    }

    fun callAccepted() {
        animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color.green.toConstraint()) }
    }
}
