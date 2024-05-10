/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.network.packets.CallFeedbackPacket
import io.github.singlerr.semaphore.regisries.CommonRegistries
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import io.github.singlerr.semaphore.utils.asImageAsync
import java.util.UUID

class UIInCall(parent: UIComponent, ownerState: PlayerContext, callerId: UUID) : UIBlock() {

    companion object {
        private val CALL_ICON =
            ResourceLocationBuilder.builder()
                .namespace(Semaphore.MOD_ID)
                .append("textures")
                .append("gui")
                .append("phone_call.png")
                .build()
    }

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

        val accept =
            UIImage(CALL_ICON.asImageAsync()).constrain {
                x = RelativeConstraint(1 / 3f)
                y = 50.pixels(true)

                width = 50.pixels()
                height = ImageAspectConstraint()
            } childOf parent

        accept.onMouseClick {
            CommonRegistries.getEventPool()
                .invoke(CallFeedbackPacket.builder().caller(callerId).callee(ownerState.owner))
        }

        val deny =
            UIImage(CALL_ICON.asImageAsync()).constrain {
                x = RelativeConstraint(2 / 3f)
                y = 50.pixels(true)

                width = 50.pixels()
                height = ImageAspectConstraint()
            } childOf parent
    }
}
