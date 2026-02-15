package org.roldy.g3d.environment.decal

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.interpolation.ColorMap
import org.roldy.core.system.DayNightSystem

class MoonDecal(
    private val camera: Camera,
    private val dayNightSystem: DayNightSystem,
    private val colorMap: ColorMap
) : PlanetDecal(AtlasLoader.moon, camera, 50f) {

    private var currentColor = Color()

    context(delta: Float)
    override fun update() {
        position.set(dayNightSystem.moonLight.direction).nor().scl(-distance).add(camera.position)
        decal.setPosition(position)
        decal.lookAt(camera.position, camera.up)
        currentColor.set(colorMap.interpolate(dayNightSystem.timeOfDay))
        val altitude = -dayNightSystem.moonLight.direction.y
        val horizonFade = MathUtils.clamp((altitude + 0.1f) / 0.2f, 0f, 1f)
        currentColor.a *= horizonFade
        decal.setColor(currentColor)

    }
}