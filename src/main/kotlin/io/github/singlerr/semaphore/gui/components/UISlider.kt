/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.percent
import java.awt.Color
import org.apache.logging.log4j.LogManager

class UISlider(
    parent: UIComponent,
    private val initialWidth: RelativeConstraint,
    private val min: Float,
    private val max: Float,
    private val defaultValue: Float,
    private val barColor: Color,
    private val valueConsumer: (Float, Float) -> Unit
) : UIBlock(color = parent.getColor()) {
  companion object {
    private val log = LogManager.getLogger(UISlider::class.java)
  }

  private var isDragging = false
  private var dragOffset: Pair<Float, Float> = 0f to 0f
  init {

    val bar =
        UIRoundedRectangle(radius = 5f).constrain {
          x = 0.pixels()
          y = CenterConstraint()
          width = FillConstraint()
          height = 10.percent() boundTo this@UISlider
          color = barColor.toConstraint()
        } childOf this effect OutlineEffect(color = Color.black, width = 0.5f)

    val handle =
        UICircle(radius = 3f, color = Color.black).constrain {
          x = 0.pixels() boundTo bar
          y = CenterConstraint()
          width = 10.pixels()
          height = 100.percent()
        } childOf this

    handle.onMouseClick { isDragging = true }.onMouseRelease { isDragging = false }
    bar.onMouseDrag { mouseX, mouseY, mouseButton ->
      if (!isDragging) return@onMouseDrag
      val newX = kotlin.math.min(kotlin.math.max(mouseX, 0f), 27f).toInt()
      val p = newX / 27f
      valueConsumer(p, p * (max - min))
      handle.setX(newX.pixels())
    }
  }
}
