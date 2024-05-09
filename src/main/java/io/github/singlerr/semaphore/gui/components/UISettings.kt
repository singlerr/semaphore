/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import io.github.singlerr.semaphore.state.player.PlayerContext
import java.awt.Color

class UISettings(parent: UIComponent, states: List<PlayerContext>) : UIBlock() {

    init {

        val selectionState = BasicState(false)

        val bellRing =
            UIBlock().constrain {
                x = CenterConstraint()
                y = SiblingConstraint() + 10.pixels()

                width = RelativeConstraint()
                height = 30.pixels()

                color =
                    if (!selectionState.get()) {
                        Color.green.toConstraint()
                    } else {
                        Color.gray.toConstraint()
                    }
            } childOf parent

        val vibration =
            UIBlock().constrain {
                x = CenterConstraint() boundTo bellRing
                y = SiblingConstraint() + 10.pixels()

                width = RelativeConstraint() boundTo bellRing
                height = 30.pixels()
                color =
                    if (selectionState.get()) {
                        Color.green.toConstraint()
                    } else {
                        Color.gray.toConstraint()
                    }
            } childOf parent
    }
}
