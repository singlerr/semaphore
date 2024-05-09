/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.state.player.PlayerContext

class UIVolumes(parent: UIComponent, states: List<PlayerContext>) {

    val component: UIComponent

    init {
        component =
            ScrollComponent(innerPadding = 2f, scrollDirection = ScrollComponent.Direction.Vertical)
                .constrain {
                    x = 30.percent() boundTo parent
                    y = 10.pixels() boundTo parent

                    width = 40.percent() boundTo parent
                    height = 90.percent() boundTo parent
                }
                .onKeyType { typedChar, keyCode -> } childOf parent

        states.forEach { ctx -> component.addChild(UIVolume(ctx.owner)) }
    }
}
