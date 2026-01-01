package org.roldy.gui.general.progressBar

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import org.roldy.core.utils.copyTo
import org.roldy.gui.GuiContext
import org.roldy.gui.TextManager
import org.roldy.gui.general.label
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.el.UIWidget
import org.roldy.rendering.g2d.gui.el.image
import org.roldy.rendering.g2d.gui.el.table
import org.roldy.rendering.g2d.gui.el.uiProgressBar
import kotlin.math.abs
import kotlin.math.ceil


object LoadingText : ImperativeValue<TextManager>
object Progress : ImperativeValue<Float>

@Scene2dDsl
context(gui: GuiContext)
fun <S> UIWidget<S>.loadingBar(
    init: context(GuiContext) (@Scene2dDsl ImperativeActionDelegate).(S) -> Unit = {}
) {
    delegate {
        table(true) { storage ->
            table(true) {
                val backgroundLeft = gui.drawable { LoadingBar_Background }
                val backgroundRight = gui.drawable { LoadingBar_Background }
                table {
                    image(backgroundLeft) {
                        it.width(backgroundLeft.minWidth).height(backgroundLeft.minHeight)
                        setScaling(Scaling.none)
                        setOrigin(Align.center)
                    }
                    image(backgroundRight) {
                        it.width(backgroundRight.minWidth).height(backgroundRight.minHeight)
                        setScaling(Scaling.none)
                        setOrigin(Align.center)
                        setScale(-1f, 1f)
                    }
                }
                table {
                    val offset = 38f
                    align(Align.left)

                    uiProgressBar {
                        val left = gui.region(true) { LoadingBar_Foreground }
                        val right = gui.region(true) { LoadingBar_Foreground }
                        val overlay = mask(gui.drawable { LoadingBar_Overlay })
                        val overlayWidth = overlay.minWidth
                        val overlayHeight = overlay.minHeight
                        val minWidth = left.regionWidth
                        val minHeight = left.regionHeight.toFloat()
                        fun calculateWidth(progress: Float) =
                            (minWidth * progress).toInt().coerceIn(0, minWidth)

                        fun calculateProgress(dir: Float) =
                            abs(dir - value * 2)

                        val tmpBatch = Color()
                        val knobColor = gui.colors.primary.cpy()
                        fun Batch.colorKnob(block: () -> Unit) {
                            color copyTo tmpBatch
                            color = knobColor
                            block()
                            color = tmpBatch
                        }

                        //
                        fun knob() =
                            drawable { x, y, w, h ->
                                //left knob
                                colorKnob {
                                    left.regionWidth = calculateWidth(calculateProgress(0f))
                                    draw(left, x, y - minHeight / 2, left.regionWidth.toFloat(), minHeight)
                                }
                                //left overlay
                                overlay.draw(this, x + minWidth - overlayWidth, y - 2, overlayWidth, overlayHeight)
                                //right knob
                                colorKnob {
                                    if (value <= .5f) return@colorKnob
                                    val progress = calculateProgress(1f)
                                    val clippedWidth = calculateWidth(1f - progress)
                                    right.setRegion(
                                        clippedWidth, // Move start position
                                        right.regionY,
                                        (minWidth * progress).toInt(), // Set new width
                                        right.regionHeight
                                    )
                                    right.flip(true, false)
                                    draw(right, x + minWidth, y - minHeight / 2, right.regionWidth.toFloat(), minHeight)
                                }
                                //right overlay
                                overlay.draw(this, x + minWidth + overlayWidth, y - 2, -overlayWidth, overlayHeight)
                            }
                        style.knobBefore = knob()

                        it.padLeft(offset).padRight(offset)
                        width = backgroundLeft.minWidth
                        height = backgroundLeft.minHeight
                        init(storage)
                        value(Progress) { progress ->
                            setValue(progress)
                        }
                    }
                }

            }
            table {
                150f.also {
                    padLeft(it)
                    padRight(it)
                }
                label("Loading...") {
                    it.expand().left().top()
                    value(LoadingText, ::setText)
                }
                label("0%") {
                    it.expand().right().top()
                    value(Progress) { progress ->
                        setText("${ceil((progress * 100)).toInt()}%")
                    }
                }
            }
        }
        set(Progress, 0f)
    }
}
