/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.state.player.PlayerContext

class UIVolumes(
    parent: UIComponent,
    neighbor: UIComponent,
    ownerState: PlayerContext,
    states: List<PlayerContext>
) {

  val component: UIComponent

  init {
    component =
        ScrollComponent(innerPadding = 2f, scrollDirection = ScrollComponent.Direction.Vertical)
            .constrain {
              x = CenterConstraint()
              y = 20.pixels() boundTo neighbor

              width = FillConstraint(true) - 2.pixels()
              height = 80.percent() boundTo parent
            } childOf parent

    states.forEach { ctx ->
      if (ctx.owner != ownerState.owner) {
        component.addChild(UIVolume(ownerState, ctx.owner))
      }
    }
  }

  fun constrain(config: UIComponent.() -> Unit) {
    config(component)
  }
}
