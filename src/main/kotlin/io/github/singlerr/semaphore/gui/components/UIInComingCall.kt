/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.network.packets.CallAcceptPacket
import io.github.singlerr.semaphore.network.packets.CallRejectPacket
import io.github.singlerr.semaphore.registries.CommonRegistries
import io.github.singlerr.semaphore.sound.ClientSoundHandler
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.Resources
import io.github.singlerr.semaphore.utils.asImageAsync
import java.util.UUID

class UIInComingCall(parent: UIComponent, ownerState: PlayerContext, callerId: UUID) : UIBlock() {

    init {
        constrain {
            x = 0.pixels() boundTo parent
            y = 0.pixels() boundTo parent

            width = 100.percent() boundTo parent
            height = 100.percent() boundTo parent
        }

        val skullImageLocation = GameProfileUtils.getSkin(callerId)
        val playerSkull =
            UIPlayerSkull(skullImageLocation).constrain {
                x = CenterConstraint()
                y = 10.pixels() boundTo parent

                width = 30.percent() boundTo parent
                height = ImageAspectConstraint()
            } childOf this

        val btnSize = 20.pixels()
        val accept =
            UIImage(Resources.ICON_CALL_ACCEPT.build().asImageAsync()).constrain {
                x = RelativeConstraint(1 / 3f)
                y = 50.pixels(true)

                width = btnSize
                height = ImageAspectConstraint()
            } childOf this

        accept.onMouseClick {
            ownerState.callState = PlayerContext.CallState.IDLE
            CommonRegistries.NETWORK.sendToServer(
                CallAcceptPacket.builder()
                    .caller(PlayerContext.from(callerId))
                    .callee(ownerState)
                    .build()
            )
            ClientSoundHandler.playOkSound()
            hide(true)
            parent.removeChild(this)
        }

        val deny =
            UIImage(Resources.ICON_CALL_DENY.build().asImageAsync()).constrain {
                x = RelativeConstraint(2 / 3f)
                y = 50.pixels(true)

                width = btnSize
                height = ImageAspectConstraint()
            } childOf this

        deny.onMouseClick {
            CommonRegistries.NETWORK.sendToServer(
                CallAcceptPacket.builder()
                    .callee(PlayerContext.from(callerId))
                    .caller(ownerState)
                    .build()
            )

            ownerState.callState = PlayerContext.CallState.IDLE
            CommonRegistries.NETWORK.sendToServer(
                CallRejectPacket.builder()
                    .caller(PlayerContext.from(callerId))
                    .callee(ownerState)
                    .build()
            )
            ClientSoundHandler.playNoSound()
            hide(true)
            parent.removeChild(this@UIInComingCall)
        }

        accept
            .onMouseEnter { animate { setWidthAnimation(Animations.OUT_EXP, 0.3f, btnSize * 1.1) } }
            .onMouseLeave { animate { setWidthAnimation(Animations.OUT_EXP, 0.3f, btnSize) } }
    }
}
