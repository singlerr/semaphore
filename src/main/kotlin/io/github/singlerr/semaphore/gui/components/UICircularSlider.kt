/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIShape
import gg.essential.elementa.dsl.childOf

class UICircularSlider(private val r: Float) : UIContainer() {

  init {
    (UIShape() childOf this).apply {}
  }
}
