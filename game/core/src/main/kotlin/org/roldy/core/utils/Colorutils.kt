package org.roldy.core.utils

import com.badlogic.gdx.graphics.Color
import kotlin.random.Random

fun adjustBrightness(baseColor: Color, factor: Float): Color {
    return Color(
        baseColor.r * factor,
        baseColor.g * factor,
        baseColor.b * factor,
        baseColor.a  // Keep alpha unchanged
    )
}

fun randomColor(random: Random) =
    Color(
        Random(random.nextInt() + 1).nextFloat(),
        Random(random.nextInt() + 2).nextFloat(),
        Random(random.nextInt() + 3).nextFloat(),
        1f
    )

fun randomColor(seed: Long) =
    randomColor(Random(seed))
