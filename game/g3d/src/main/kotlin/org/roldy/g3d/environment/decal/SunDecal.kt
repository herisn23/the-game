package org.roldy.g3d.environment.decal

import com.badlogic.gdx.graphics.Camera
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.interpolation.ColorMap
import org.roldy.core.system.DayNightSystem

class SunDecal(
    private val camera: Camera,
    private val dayNightSystem: DayNightSystem,
    private val colorMap: ColorMap
) : PlanetDecal(AtlasLoader.sun, camera, 140f) {
    context(_: Float)
    override fun update() {
        position.set(dayNightSystem.sunLight.direction).nor().scl(-distance).add(camera.position)
        decal.setPosition(position)
        decal.lookAt(camera.position, camera.up)
        decal.setColor(colorMap.interpolate(dayNightSystem.timeOfDay))
    }
}