package org.roldy.core.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


fun Duration.toSeconds(): Float =
    toDouble(DurationUnit.SECONDS).toFloat()


fun Float.toDuration() =
    toDouble().seconds