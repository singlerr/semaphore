/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import de.maxhenkel.voicechat.gui.GameProfileUtils
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.events.PlaySoundCommand
import io.github.singlerr.semaphore.events.SendingCallEvent
import io.github.singlerr.semaphore.registries.ClientRegistries
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
      ownerState.callState = PlayerContext.CallState.CALLING
      val event = SendingCallEvent(currentState.owner, ownerState.owner)
      ClientRegistries.getEventPool().invoke(event)
      ClientRegistries.getEventPool()
          .invoke(PlaySoundCommand(ClientRegistries.SOUND_CALL_YES, false))
    }

    missCallImage =
        UIImage(CALL_ICON.build().asImageAsync()).constrain {
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

  companion object {
    private val CALL_ICON: ResourceLocationBuilder =
        ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_call.png")
  }
}
