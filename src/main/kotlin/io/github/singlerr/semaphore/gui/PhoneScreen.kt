/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.events.CallClosedEvent
import io.github.singlerr.semaphore.events.InComingCallFeedbackEvent
import io.github.singlerr.semaphore.events.OutComingCallFeedbackEvent
import io.github.singlerr.semaphore.events.PlaySoundCommand
import io.github.singlerr.semaphore.events.PlayerStateChangeEvent
import io.github.singlerr.semaphore.events.ReceivingCallEvent
import io.github.singlerr.semaphore.events.SendingCallEvent
import io.github.singlerr.semaphore.gui.components.*
import io.github.singlerr.semaphore.registries.ClientRegistries
import io.github.singlerr.semaphore.state.StatePool
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.EventPool
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import io.github.singlerr.semaphore.utils.asImageAsync
import io.github.singlerr.semaphore.utils.asImageAsyncNullable
import java.util.UUID
import net.minecraft.util.ResourceLocation

class PhoneScreen(statePool: StatePool, playerId: UUID) :
    WindowScreen(ElementaVersion.V5, drawDefaultBackground = false) {

  companion object {
    private val SETTINGS_ICON =
        ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("settings.png")
            .build()

    private val PHONE_FRAME: ResourceLocation =
        ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("phone_frame_bar.png")
            .build()
  }

  private val frame: UIComponent

  private val rootComponent: UIComponent

  private val settingsScreen: UISettings
  private val addressScreen: UIAddressList

  private var inCallScreen: UIInComingCall? = null
  private var outCallScreen: UIOutComingCall? = null

  private var inSettings = false

  private val ownerState: PlayerContext

  init {

    frame =
        UIImage(PHONE_FRAME.asImageAsync()).constrain {
          x = 0.pixels()
          y = 5.percent()
          width = 40.percent()
          height = 80.percent()
        } childOf window

    val states = statePool.states.map { t -> t.value }.filterIsInstance<PlayerContext>()

    ownerState = statePool.get(playerId, PlayerContext::class.java).get()

    val backgroundImage =
        ResourceLocationBuilder.builder()
            .namespace(Semaphore.MOD_ID)
            .append("textures")
            .append("gui")
            .append("backgrounds")
            .append("${playerId}.png")
            .build()
            .asImageAsyncNullable()

    val backgroundX = 27.percent() boundTo frame
    val backgroundY = 8.percent() boundTo frame
    val backgroundWidth = 46.percent() boundTo frame
    val backgroundHeight = 83.percent() boundTo frame
    val container =
        if (backgroundImage != null) {
          UIImage(backgroundImage).constrain {
            x = backgroundX
            y = backgroundY

            width = backgroundWidth
            height = backgroundHeight
          } childOf frame
        } else {
          UIBlock().constrain {
            x = backgroundX
            y = backgroundY

            width = backgroundWidth
            height = backgroundHeight
          } childOf frame
        }

    rootComponent = container
    addressScreen = UIAddressList(container, ownerState, states)
    settingsScreen =
        UISettings(container, ownerState, states).constrain {
          x = 0.pixels() boundTo container
          y = 0.pixels() boundTo container

          width = 100.percent() boundTo container
          height = 100.percent() boundTo container
        } childOf container

    settingsScreen.hide(true)

    val settingsBtn =
        UIImage(SETTINGS_ICON.asImageAsync()).constrain {
          x = 10.pixels() boundTo container
          y = 20.pixels(true) boundTo frame

          width = 10.pixels()
          height = 10.pixels()
        } childOf frame

    settingsBtn.onMouseClick {
      if (inSettings) {
        addressScreen.component.unhide()
        settingsScreen.hide(true)
      } else {
        addressScreen.component.hide(true)
        settingsScreen.unhide()
      }

      inSettings = !inSettings
    }

    frame.onMouseClick {
      ClientRegistries.getEventPool()
          .invoke(PlaySoundCommand(ClientRegistries.SOUND_PHONE_TOUCH, false))
    }

    Inspector(window).constrain {
      x = 10.pixels(true)
      y = 10.pixels(true)
    } childOf window
  }

  fun register(eventPool: EventPool) {
    eventPool.subscribe(ReceivingCallEvent::class.java, this::onReceivingCall)
    eventPool.subscribe(SendingCallEvent::class.java, this::onSendingCall)
    eventPool.subscribe(InComingCallFeedbackEvent::class.java, this::onInComingCallFeedback)
    eventPool.subscribe(OutComingCallFeedbackEvent::class.java, this::onOutComingCallFeedback)
    eventPool.subscribe(CallClosedEvent::class.java, this::onCallClosed)
  }

  private fun onReceivingCall(e: ReceivingCallEvent) {
    if (ownerState.callState == PlayerContext.CallState.IDLE) {
      inCallScreen?.unhide()
    }
  }

  private fun onSendingCall(e: SendingCallEvent) {
    outCallScreen = UIOutComingCall(rootComponent, ownerState, e.callee) childOf rootComponent
    outCallScreen?.unhide(true)
  }

  private fun onInComingCallFeedback(e: InComingCallFeedbackEvent) {
    Window.enqueueRenderOperation {
      if (e.feedback != PlayerContext.CallFeedback.ACCEPT) {
        outCallScreen?.callNotAvailable()
      } else {
        outCallScreen?.callAccepted()
      }
    }
  }

  private fun onOutComingCallFeedback(e: OutComingCallFeedbackEvent) {
    outCallScreen?.hide(true)
    outCallScreen = null
  }

  private fun onPlayerStateChange(e: PlayerStateChangeEvent) {
    addressScreen.update(ownerState, e.state)
  }

  private fun onCallClosed(e: CallClosedEvent) {
    inCallScreen?.hide(true)
    outCallScreen?.hide(true)
  }
}
