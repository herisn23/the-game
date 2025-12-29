package org.roldy.core.utils

import kotlin.time.Duration
import kotlin.time.DurationUnit


fun Duration.toSeconds(): Float =
    toDouble(DurationUnit.SECONDS).toFloat()