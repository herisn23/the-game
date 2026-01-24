package org.roldy.core.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


fun Duration.toSeconds(): Float =
    toDouble(DurationUnit.SECONDS).toFloat()


fun Float.toDuration() =
    toDouble().seconds


fun Duration.progress(duration: Duration, min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE): Float =
    (this / duration).toFloat().coerceIn(min, max)