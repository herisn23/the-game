package org.roldy.rendering.g2d

import com.badlogic.gdx.math.Vector2
import org.roldy.core.x

class PivotDefaults(
    val x: Float = 0f,
    val y: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val width: Float = 0f,
    val height: Float = 0f,
    val parentHeight: Float = 0f,
    val parentWidth: Float = 0f
) {
    fun pivot() =
        Pivot(this)
}

class Pivot(
    val defaults: PivotDefaults = PivotDefaults()
) {
    class Parent(
        val pivot: Pivot,
        val parentWidth: Float = 0f,
        val parentHeight: Float = 0f,
    ) {
        fun pivot() = pivot

        fun center() =
            PivotPosition.centerToParent(pivot.x, pivot.y, parentWidth, parentHeight).apply()

        fun top() =
            center().also {
                pivot.y += parentHeight / 2
            }


        private fun Vector2.apply(): Parent = let {
            this@Parent.pivot.x = x
            this@Parent.pivot.y = y
            this@Parent
        }
    }

    var x: Float = defaults.x
    var y: Float = defaults.y
    var scaleX: Float = defaults.scaleX
    var scaleY: Float = defaults.scaleY
    val width: Float = defaults.width
    val height: Float = defaults.height

    val parent = Parent(this, defaults.parentWidth, defaults.parentHeight)


    fun setPosition(x: Float, y: Float) = apply {
        this.x = x
        this.y = y
    }

    fun setScale(scaleXY: Float) = apply {
        this.scaleX = scaleXY
        this.scaleY = scaleXY
    }

    fun setScale(scaleX: Float, scaleY: Float) = apply {
        this.scaleX = scaleX
        this.scaleY = scaleY
    }

    fun center() =
        PivotPosition.center(x, y, width * scaleX, height * scaleY).apply()

    fun left() =
        (PivotPosition.left(x, width * scaleX) x y).apply()

    fun parent() = parent


    private fun Vector2.apply(): Pivot = let {
        this@Pivot.x = x
        this@Pivot.y = y
        this@Pivot
    }
}

object PivotPosition {
    fun center(axis: Float, size: Float): Float = axis - size / 2

    fun center(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
    ): Vector2 = center(x, width) x center(y, height)

    fun centerToParent(axis: Float, parentSize: Float): Float =
        axis + parentSize / 2

    fun centerToParent(
        x: Float,
        y: Float,
        parentWidth: Float,
        parentHeight: Float
    ): Vector2 =
        centerToParent(x, parentWidth) x centerToParent(y, parentHeight)

    fun left(
        x: Float,
        width: Float
    ): Float = x - width
}