package org.roldy.core.utils

import com.badlogic.gdx.graphics.Color
import kotlin.random.Random

infix fun Color.brighter(factor: Float): Color {
    return Color(
        r * factor,
        g * factor,
        b * factor,
        a  // Keep alpha unchanged
    )
}

infix fun Color.alpha(alpha: Float): Color {
    return Color(
        r,
        g,
        b,
        alpha
    )
}

fun randomColor(random: Random) =
    Color(
        Random(random.nextInt() + 1).nextFloat(),
        Random(random.nextInt() + 2).nextFloat(),
        Random(random.nextInt() + 3).nextFloat(),
        1f
    )

fun transparentColor(alpha: Float): Color =
    Color(
        1f, 1f, 1f,
        alpha
    )

fun randomColor(seed: Long) =
    randomColor(Random(seed))
