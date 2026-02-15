package org.roldy.g3d.environment.decal

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.disposable.disposableList
import org.roldy.core.system.DayNightSystem
import org.roldy.g3d.environment.moonColorMap
import org.roldy.g3d.environment.sunColorMap

class PlanetsBillboards(
    val camera: Camera,
    decals: List<PlanetDecal>,
) : AutoDisposableAdapter() {

    private val decals by disposableList { decals }

    private val decalBatch by disposable { DecalBatch(CameraGroupStrategy(camera)) }

    context(deltaTime: Float)
    fun render() {
        // Force reset everything
        Gdx.gl.glUseProgram(0)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0)
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDisable(GL20.GL_CULL_FACE)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)

        decals.forEach {
            it.update()
            decalBatch.add(it.decal)
        }

        decalBatch.flush()

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
    }

}


fun createPlanetsBillboards(
    camera: Camera,
    dayNightSystem: DayNightSystem,
): PlanetsBillboards {
    return PlanetsBillboards(
        camera,
        listOf(
            SunDecal(camera, dayNightSystem, sunColorMap),
            MoonDecal(camera, dayNightSystem, moonColorMap)
        )
    )
}