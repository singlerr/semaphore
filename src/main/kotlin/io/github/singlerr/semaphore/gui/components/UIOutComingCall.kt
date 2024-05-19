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
import io.github.singlerr.semaphore.events.CallClosedEvent
import io.github.singlerr.semaphore.events.PlayerStateChangeEvent
import io.github.singlerr.semaphore.registries.ClientRegistries
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.Resources
import io.github.singlerr.semaphore.utils.asImageAsync
import java.awt.Color
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
        UIPlayerSkull(skullImageLocation).constrain {
          x = CenterConstraint()
          y = 10.pixels() boundTo parent

          width = 30.percent() boundTo parent
          height = ImageAspectConstraint()
        } childOf this

    val cancel =
        UIImage(Resources.ICON_CALL_DENY.build().asImageAsync()).constrain {
          x = CenterConstraint()
          y = 50.pixels(true)

          width = 20.pixels()
          height = ImageAspectConstraint()
        } childOf this

    cancel.onMouseClick {
      val event = PlayerStateChangeEvent(ownerState)
      ClientRegistries.getEventPool().invoke(event)
      ClientRegistries.getEventPool().invoke(CallClosedEvent(ownerState.owner, calleeId))
      this@UIOutComingCall.hide(true)
    }

    messageLabel =
        UIWrappedText(I18n.format("gui.phonescreen.misscall")).constrain {
          x = CenterConstraint()
          y = 100.pixels(true)

          width = 100.percent() boundTo parent
        } childOf this

    messageLabel.hide(true)
  }

  fun callNotAvailable() {
    messageLabel.unhide()
  }

  fun callAccepted() {
    animate { setColorAnimation(Animations.OUT_EXP, 0.5f, Color.green.toConstraint()) }
  }
}
