package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.scenes.scene2d.utils.Drawable


/**
 * Stretch modes similar to Unity's RectTransform anchor presets
 */
enum class StretchMode {
    // Fixed anchors (no stretching)
    TOP_LEFT, TOP_CENTER, TOP_RIGHT,
    MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT,
    BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,

    // Horizontal stretch
    TOP_STRETCH, MIDDLE_STRETCH, BOTTOM_STRETCH,

    // Vertical stretch
    STRETCH_LEFT, STRETCH_CENTER, STRETCH_RIGHT,

    // Full stretch
    STRETCH_FULL
}

/**
 * Stretch the drawable within parent bounds using Unity-style anchoring
 * @param parentWidth Width of the parent container
 * @param parentHeight Height of the parent container
 * @param mode The stretch/anchor mode to use
 * @param left Left offset/margin from anchor
 * @param right Right offset/margin from anchor
 * @param top Top offset/margin from anchor
 * @param bottom Bottom offset/margin from anchor
 * @param width Fixed width (used for non-stretched dimensions)
 * @param height Fixed height (used for non-stretched dimensions)
 */
fun Draw.stretch(
    parentWidth: Float,
    parentHeight: Float,
    mode: StretchMode,
    left: Float = 0f,
    right: Float = 0f,
    top: Float = 0f,
    bottom: Float = 0f,
    width: Float = 100f,
    height: Float = 100f
) {
    val (x, y, w, h) = calculateStretchBounds(
        parentWidth, parentHeight, mode,
        left, right, top, bottom, width, height
    )

    draw(x, y, w, h)
}

/**
 * Calculate bounds based on stretch mode
 */
private fun calculateStretchBounds(
    parentWidth: Float,
    parentHeight: Float,
    mode: StretchMode,
    left: Float,
    right: Float,
    top: Float,
    bottom: Float,
    width: Float,
    height: Float
): StretchBounds {
    return when (mode) {
        // Top row - fixed size
        StretchMode.TOP_LEFT -> StretchBounds(
            left, parentHeight - top - height, width, height
        )

        StretchMode.TOP_CENTER -> StretchBounds(
            (parentWidth - width) / 2 + left, parentHeight - top - height, width, height
        )

        StretchMode.TOP_RIGHT -> StretchBounds(
            parentWidth - right - width, parentHeight - top - height, width, height
        )

        // Middle row - fixed size
        StretchMode.MIDDLE_LEFT -> StretchBounds(
            left, (parentHeight - height) / 2 + bottom, width, height
        )

        StretchMode.MIDDLE_CENTER -> StretchBounds(
            (parentWidth - width) / 2 + left, (parentHeight - height) / 2 + bottom, width, height
        )

        StretchMode.MIDDLE_RIGHT -> StretchBounds(
            parentWidth - right - width, (parentHeight - height) / 2 + bottom, width, height
        )

        // Bottom row - fixed size
        StretchMode.BOTTOM_LEFT -> StretchBounds(
            left, bottom, width, height
        )

        StretchMode.BOTTOM_CENTER -> StretchBounds(
            (parentWidth - width) / 2 + left, bottom, width, height
        )

        StretchMode.BOTTOM_RIGHT -> StretchBounds(
            parentWidth - right - width, bottom, width, height
        )

        // Horizontal stretch
        StretchMode.TOP_STRETCH -> StretchBounds(
            left, parentHeight - top - height, parentWidth - left - right, height
        )

        StretchMode.MIDDLE_STRETCH -> StretchBounds(
            left, (parentHeight - height) / 2 + bottom, parentWidth - left - right, height
        )

        StretchMode.BOTTOM_STRETCH -> StretchBounds(
            left, bottom, parentWidth - left - right, height
        )

        // Vertical stretch
        StretchMode.STRETCH_LEFT -> StretchBounds(
            left, bottom, width, parentHeight - top - bottom
        )

        StretchMode.STRETCH_CENTER -> StretchBounds(
            (parentWidth - width) / 2 + left, bottom, width, parentHeight - top - bottom
        )

        StretchMode.STRETCH_RIGHT -> StretchBounds(
            parentWidth - right - width, bottom, width, parentHeight - top - bottom
        )

        // Full stretch
        StretchMode.STRETCH_FULL -> StretchBounds(
            left, bottom, parentWidth - left - right, parentHeight - top - bottom
        )
    }
}

private data class StretchBounds(val x: Float, val y: Float, val width: Float, val height: Float)

// ========== Convenience Extension Functions ==========

// Fixed position anchors
fun Draw.topLeft(
    parentWidth: Float,
    parentHeight: Float,
    left: Float = 0f,
    top: Float = 0f,
    width: Float,
    height: Float
) {
    stretch(parentWidth, parentHeight, StretchMode.TOP_LEFT, left, 0f, top, 0f, width, height)
}

fun Draw.topCenter(parentWidth: Float, parentHeight: Float, top: Float = 0f, width: Float, height: Float) {
    stretch(parentWidth, parentHeight, StretchMode.TOP_CENTER, 0f, 0f, top, 0f, width, height)
}

fun Draw.topRight(
    parentWidth: Float,
    parentHeight: Float,
    right: Float = 0f,
    top: Float = 0f,
    width: Float,
    height: Float
) {
    stretch(parentWidth, parentHeight, StretchMode.TOP_RIGHT, 0f, right, top, 0f, width, height)
}

fun Draw.middleLeft(parentWidth: Float, parentHeight: Float, left: Float = 0f, width: Float, height: Float) {
    stretch(parentWidth, parentHeight, StretchMode.MIDDLE_LEFT, left, 0f, 0f, 0f, width, height)
}

fun Draw.middleCenter(parentWidth: Float, parentHeight: Float, width: Float, height: Float) {
    stretch(parentWidth, parentHeight, StretchMode.MIDDLE_CENTER, 0f, 0f, 0f, 0f, width, height)
}

fun Draw.middleRight(parentWidth: Float, parentHeight: Float, right: Float = 0f, width: Float, height: Float) {
    stretch(parentWidth, parentHeight, StretchMode.MIDDLE_RIGHT, 0f, right, 0f, 0f, width, height)
}

fun Draw.bottomLeft(
    parentWidth: Float,
    parentHeight: Float,
    left: Float = 0f,
    bottom: Float = 0f,
    width: Float,
    height: Float
) {
    stretch(parentWidth, parentHeight, StretchMode.BOTTOM_LEFT, left, 0f, 0f, bottom, width, height)
}

fun Draw.bottomCenter(parentWidth: Float, parentHeight: Float, bottom: Float = 0f, width: Float, height: Float) {
    stretch(parentWidth, parentHeight, StretchMode.BOTTOM_CENTER, 0f, 0f, 0f, bottom, width, height)
}

fun Draw.bottomRight(
    parentWidth: Float,
    parentHeight: Float,
    right: Float = 0f,
    bottom: Float = 0f,
    width: Float,
    height: Float
) {
    stretch(parentWidth, parentHeight, StretchMode.BOTTOM_RIGHT, 0f, right, 0f, bottom, width, height)
}

// Horizontal stretch
fun Draw.topStretch(
    parentWidth: Float,
    parentHeight: Float,
    left: Float = 0f,
    right: Float = 0f,
    top: Float = 0f,
    height: Float
) {
    stretch(parentWidth, parentHeight, StretchMode.TOP_STRETCH, left, right, top, 0f, 0f, height)
}

fun Draw.middleStretch(parentWidth: Float, parentHeight: Float, left: Float = 0f, right: Float = 0f, height: Float) {
    stretch(parentWidth, parentHeight, StretchMode.MIDDLE_STRETCH, left, right, 0f, 0f, 0f, height)
}

fun Draw.bottomStretch(
    parentWidth: Float,
    parentHeight: Float,
    left: Float = 0f,
    right: Float = 0f,
    bottom: Float = 0f,
    height: Float
) {
    stretch(parentWidth, parentHeight, StretchMode.BOTTOM_STRETCH, left, right, 0f, bottom, 0f, height)
}

// Vertical stretch
fun Draw.stretchLeft(
    parentWidth: Float,
    parentHeight: Float,
    left: Float = 0f,
    top: Float = 0f,
    bottom: Float = 0f,
    width: Float
) {
    stretch(parentWidth, parentHeight, StretchMode.STRETCH_LEFT, left, 0f, top, bottom, width, 0f)
}

fun Draw.stretchCenter(parentWidth: Float, parentHeight: Float, top: Float = 0f, bottom: Float = 0f, width: Float) {
    stretch(parentWidth, parentHeight, StretchMode.STRETCH_CENTER, 0f, 0f, top, bottom, width, 0f)
}

fun Draw.stretchRight(
    parentWidth: Float,
    parentHeight: Float,
    right: Float = 0f,
    top: Float = 0f,
    bottom: Float = 0f,
    width: Float
) {
    stretch(parentWidth, parentHeight, StretchMode.STRETCH_RIGHT, 0f, right, top, bottom, width, 0f)
}

// Full stretch
fun Draw.stretchFull(
    parentWidth: Float,
    parentHeight: Float,
    left: Float = 0f,
    right: Float = 0f,
    top: Float = 0f,
    bottom: Float = 0f
) {
    stretch(parentWidth, parentHeight, StretchMode.STRETCH_FULL, left, right, top, bottom, 0f, 0f)
}

@DrawableDsl
fun stretch(
    mode: StretchMode,
    uniform: Float = 0f,
    left: Float = uniform,
    right: Float = uniform,
    top: Float = uniform,
    bottom: Float = uniform
): Redraw = { x, y, w, h, draw ->
    Draw { x, y, w, h ->
        draw(x, y, w, h)
    }.stretch(
        x + w, y + h,
        mode, left, right, top, bottom, w, h
    )
}

fun Drawable.pad(
    uniform: Float = 0f
) = pad(uniform * 2, uniform, uniform, uniform * 2)

fun Drawable.pad(
    top: Float = 0f,
    left: Float = 0f,
    bottom: Float = 0f,
    right: Float = 0f
) = redraw { x, y, w, h, draw ->
    draw(x + left, y + bottom, w - right, h - top)
}