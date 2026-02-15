package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import org.roldy.core.system.DayNightSystem


fun createDayNightSystemInstance(
    environment: Environment,
    sunLight: DirectionalLight,
    moonLight: DirectionalLight,
) = DayNightSystem(
    environment,
    sunLight,
    moonLight,
    ambientColorMap,
    atmosphereColorMap,
    sunIntensityMap,
    moonIntensityMap
)