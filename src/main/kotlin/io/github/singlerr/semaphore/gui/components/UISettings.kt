/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.toConstraint
import io.github.singlerr.semaphore.config.ModConfig
import io.github.singlerr.semaphore.state.player.PlayerContext
import java.awt.Color

class UISettings(parent: UIComponent, ownerState: PlayerContext, states: List<PlayerContext>) :
    UIBlock() {
  init {

    val bellRingState = BasicState(true)
    val vibrationState = BasicState(false)

    val container =
        UIContainer().constrain {
          x = CenterConstraint()
          y = 10.pixels()

          width = 80.percent() boundTo this@UISettings
          height = 10.pixels() boundTo this@UISettings
        } childOf this

    val bellRing =
        UIRoundedRectangle(radius = 10f).constrain {
          x = SiblingConstraint(padding = 2f)
          y = CenterConstraint()
          width = RelativeConstraint(1 / 2f)
          height = ChildBasedMaxSizeConstraint()

          color = bellRingState.map(this@UISettings::toColor).toConstraint()
        } childOf container

    val bellRingText =
        UIText("Bell").constrain {
          x = CenterConstraint() boundTo bellRing
          y = CenterConstraint() boundTo bellRing
          width = 50.percent() boundTo bellRing
          height = AspectConstraint() boundTo bellRing
        } childOf bellRing

    val vibration =
        UIRoundedRectangle(radius = 10f).constrain {
          x = SiblingConstraint(padding = 2f)
          y = CenterConstraint()
          width = RelativeConstraint(1 / 2f)
          height = ChildBasedMaxSizeConstraint()

          color = vibrationState.map(this@UISettings::toColor).toConstraint()
        } childOf container
    val vibrationText =
        UIText("Vibration").constrain {
          x = CenterConstraint() boundTo vibration
          y = CenterConstraint() boundTo vibration
          width = 50.percent() boundTo vibration
          height = AspectConstraint() boundTo vibration
        } childOf vibration

    bellRing.onMouseClick {
      bellRingState.set(true)
      vibrationState.set(false)
      ModConfig.bellRing = true
    }

    vibration.onMouseClick {
      vibrationState.set(true)
      bellRingState.set(false)
      ModConfig.bellRing = false
    }
    val volumeSettings = UIVolumes(this, container, ownerState, states)
  }

  private fun toColor(flag: Boolean): Color {
    return if (flag) Color.green else Color.gray
  }
}
