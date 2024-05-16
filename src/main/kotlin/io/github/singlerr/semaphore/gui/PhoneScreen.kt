/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.Semaphore
import io.github.singlerr.semaphore.events.CallClosedEvent
import io.github.singlerr.semaphore.events.InComingCallFeedbackEvent
import io.github.singlerr.semaphore.events.OutComingCallFeedbackEvent
import io.github.singlerr.semaphore.events.PlaySoundCommand
import io.github.singlerr.semaphore.events.PlayerStateChangeEvent
import io.github.singlerr.semaphore.events.ReceivingCallEvent
import io.github.singlerr.semaphore.events.SendingCallEvent
import io.github.singlerr.semaphore.events.StopSoundCommand
import io.github.singlerr.semaphore.gui.components.*
import io.github.singlerr.semaphore.network.packets.CallFeedbackPacket
import io.github.singlerr.semaphore.network.packets.PlayerStatePacket
import io.github.singlerr.semaphore.registries.ClientRegistries
import io.github.singlerr.semaphore.registries.CommonRegistries
import io.github.singlerr.semaphore.sound.AudioPlayer
import io.github.singlerr.semaphore.state.StatePool
import io.github.singlerr.semaphore.state.player.PlayerContext
import io.github.singlerr.semaphore.utils.ResourceLocationBuilder
import io.github.singlerr.semaphore.utils.asImageAsync
import io.github.singlerr.semaphore.utils.asImageAsyncNullable
import java.util.UUID
import net.minecraft.util.ResourceLocation

class PhoneScreen(statePool: StatePool, private val playerId: UUID) :
    WindowScreen(ElementaVersion.V5) {

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
            .append("phone_frame.png")
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

    val container =
        if (backgroundImage != null) {
          UIImage(backgroundImage).constrain {
            x = 30.percent() boundTo frame
            y = 10.pixels() boundTo frame

            width = 40.percent() boundTo frame
            height = 80.percent() boundTo frame
          } childOf frame
        } else {
          UIBlock().constrain {
            x = 30.percent() boundTo frame
            y = 10.pixels() boundTo frame

            width = 40.percent() boundTo frame
            height = 80.percent() boundTo frame
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
          y = 10.pixels(true) boundTo frame

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

    //        Inspector(window).constrain {
    //            x = 10.pixels(true)
    //            y = 10.pixels(true)
    //        } childOf window
  }

  fun onReceivingCall(e: ReceivingCallEvent) {
    if (ownerState.callState != PlayerContext.CallState.IDLE) {
      CommonRegistries.NETWORK.sendToServer(
          CallFeedbackPacket.builder()
              .callFeedback(PlayerContext.CallFeedback.DENY_IN_CALL)
              .caller(e.caller)
              .callee(e.callee)
              .build())
      ownerState.missCalls[e.caller]?.incrementAndGet()
    } else {
      ownerState.opponent = e.caller
      ownerState.callState = PlayerContext.CallState.RECEIVING_CALL
      inCallScreen?.unhide()
    }
  }

  fun onSendingCall(e: SendingCallEvent) {
    outCallScreen = UIOutComingCall(rootComponent, ownerState, e.callee) childOf rootComponent
    outCallScreen?.unhide(true)
    ownerState.missCalls[e.callee]?.set(0)
    addressScreen.update(CommonRegistries.statePool)
  }

  fun onInComingCallFeedback(e: InComingCallFeedbackEvent) {
    Window.enqueueRenderOperation {
      if (e.feedback != PlayerContext.CallFeedback.ACCEPT) {
        AudioPlayer.play(ClientRegistries.MISS_CALL_SOUND)
        outCallScreen?.callNotAvailable()
      } else {
        ownerState.opponent = e.callee
        ownerState.callState = PlayerContext.CallState.IN_CALL
        outCallScreen?.callAccepted()
      }
    }
  }

  fun onOutComingCallFeedback(e: OutComingCallFeedbackEvent) {
    if (e.feedback == PlayerContext.CallFeedback.ACCEPT) {
      ownerState.callState = PlayerContext.CallState.IN_CALL
      ownerState.opponent = e.caller
      CommonRegistries.NETWORK.sendToServer(
          PlayerStatePacket.builder().id(ownerState.owner).state(ownerState).build())
      ClientRegistries.getEventPool().invoke(StopSoundCommand())
      return
    }
  }

  fun onPlayerStateChange(e: PlayerStateChangeEvent) {
    addressScreen.update(ownerState, e.state)
  }

  fun onCallClosed(e: CallClosedEvent) {}
}
