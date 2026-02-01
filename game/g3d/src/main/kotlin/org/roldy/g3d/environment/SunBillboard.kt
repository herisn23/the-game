package org.roldy.g3d.environment

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Vector3
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
        Texture(Gdx.files.internal("sun.png")).apply {
            setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        }
    }

    init {
        decal = Decal.newDecal(size, size, TextureRegion(texture), true)
        decal.setColor(light.color)
    }

    fun updatePosition() {
        position.set(light.direction).nor().scl(-distance).add(camera.position)
        decal.setPosition(position)
    }

    fun render() {
        // Billboard always faces camera
        decal.lookAt(camera.position, camera.up)
        decalBatch.add(decal)
        decalBatch.flush()
    }
}