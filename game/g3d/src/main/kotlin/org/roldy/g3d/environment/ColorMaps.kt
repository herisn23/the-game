package org.roldy.g3d.environment

import org.roldy.core.interpolation.ColorMap
import org.roldy.core.interpolation.FloatMap
import org.roldy.core.utils.hex


val ambientColorMap = ColorMap(
    mapOf(
        0.00f to hex("1a3040"),  // Midnight - cool moonlit blue-grey
        0.15f to hex("1a3040"),  // Deep night - sustained moonlight
        0.20f to hex("2a3a5a"),  // Pre-dawn - moon still strong
        0.25f to hex("4d3333"),  // Sunrise - warm transition
        0.30f to hex("666666"),  // Morning
        0.50f to hex("999999"),  // Noon
        0.70f to hex("666666"),  // Afternoon
        0.75f to hex("4d3333"),  // Sunset
        0.80f to hex("2a2a4d"),  // Dusk - transitioning to moonlight
        0.85f to hex("1a3040"),  // Night - moonlight takes over
        1.00f to hex("1a3040")   // Midnight - cool moonlit blue-grey
    )
)

val atmosphereColorMap = ColorMap(
    mapOf(
        0.00f to hex("0a1a2e"),  // Midnight - dark blue, moon-touched
        0.15f to hex("0a1a2e"),  // Deep night - steady moonlit sky
        0.20f to hex("1a2844"),  // Pre-dawn - moon fading, hint of indigo
        0.23f to hex("3d2a5c"),  // Early dawn - purple-blue transition
        0.25f to hex("ff8033"),  // Sunrise - orange
        0.30f to hex("ffcc99"),  // Early morning - warm
        0.50f to hex("fff2e6"),  // Noon - bright white
        0.70f to hex("ffcc99"),  // Late afternoon - warm
        0.75f to hex("ff6619"),  // Sunset - deep orange
        0.78f to hex("6b3a8a"),  // Early dusk - purple
        0.80f to hex("2a1f5c"),  // Dusk - deep purple, moon rising
        0.85f to hex("0f1e3d"),  // Night - moonlit deep blue
        1.00f to hex("0a1a2e")   // Midnight - dark blue, moon-touched
    )
)

val sunColorMap = ColorMap(
    mapOf(
        0.00f to hex("1a1a4d"),    // Midnight - dark blue
        0.25f to hex("ffb763"),// Sunrise - orange
        0.30f to hex("ffecb4"),
        0.40f to hex("fff6ea"),    // Early morning - warm
        0.50f to hex("f9fff6"),   // Noon - bright white
        0.60f to hex("fff6ea"),    // Late afternoon - warm
        0.65f to hex("ffead1"),    // Late afternoon - warm
        0.70f to hex("ffe0b3"),    // Late afternoon - warm
        0.73f to hex("ffb763"),    // Sunset - deep orange
        0.75f to hex("ff984c"),    // Sunset - deep orange
        0.80f to hex("4d3366"),    // Dusk - purple
        0.85f to hex("1a1a4d"),    // Night - dark blue
        1.00f to hex("1a1a4d")     // Midnight
    )
)

val moonColorMap = ColorMap(
    mapOf(
        0.00f to hex("b3c0e6"),
        0.20f to hex("b3c0e6"),
        0.25f to hex("ffb380"),
        0.30f to hex("e6e6f2"),
        0.50f to hex("e6e6f2"),
        0.70f to hex("e6e6f2"),
        0.75f to hex("ff9966"),
        0.80f to hex("9980b3"),
        0.85f to hex("b3c0e6"),
        1.00f to hex("b3c0e6")
    )
)

val sunIntensityMap = FloatMap(
    mapOf(
        0.00f to 0.0f,   // Midnight - no sun
        0.20f to 0.0f,   // Pre-dawn
        0.25f to 0.3f,   // Sunrise
        0.30f to 0.7f,   // Morning
        0.50f to 1.0f,   // Noon - full intensity
        0.70f to 0.7f,   // Afternoon
        0.75f to 0.3f,   // Sunset
        0.80f to 0.0f,   // Dusk
        0.85f to 0.0f,   // Night
        1.00f to 0.0f    // Midnight
    )
)

val moonIntensityMap = FloatMap(
    mapOf(
        0.00f to 0.25f,  // midnight — full moon brightness
        0.20f to 0.20f,  // pre-dawn — still visible
        0.25f to 0.0f,   // sunrise — moon fades out
        0.50f to 0.0f,   // noon — no moon
        0.75f to 0.0f,   // sunset — moon starts appearing
        0.80f to 0.15f,  // early night
        1.00f to 0.25f   // back to midnight
    )
)