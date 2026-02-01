package org.roldy.g3d.environment

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Vector3
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable

class SunBillboard(
    private val camera: Camera,
    private val light: DirectionalLight,
    private val distance: Float = 5000f,
    private val size: Float = 400f
) : AutoDisposableAdapter() {
    private val decal: Decal
    private val position = Vector3()
    private val decalBatch by disposable { DecalBatch(CameraGroupStrategy(camera)) }
    private val texture by disposable {
        Texture(AtlasLoader.sun).apply {
            setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        }
    }

    init {
        decal = Decal.newDecal(size, size, TextureRegion(texture), true)
        decal.setColor(Color.WHITE)
    }

    fun updatePosition() {
        position.set(light.direction).nor().scl(-distance).add(camera.position)
        decal.setPosition(position)
        decal.lookAt(camera.position, camera.up)
    }

    fun render() {
        updatePosition()
        // Force reset everything
        Gdx.gl.glUseProgram(0)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0)
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDisable(GL20.GL_CULL_FACE)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE)

        position.set(light.direction).nor().scl(-distance).add(camera.position)
        decal.setPosition(position)
        decal.lookAt(camera.position, camera.up)

        decalBatch.add(decal)
        decalBatch.flush()

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
    }
}