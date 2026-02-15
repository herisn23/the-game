package org.roldy.g3d.environment.decal

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.math.Vector3
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable

abstract class PlanetDecal(
    private val texturePath: FileHandle,
    camera: Camera,
    size: Float
) : AutoDisposableAdapter() {
    val distance = camera.far - 50f
    val position = Vector3()
    val decal: Decal
    private val texture by disposable {
        Texture(texturePath).apply {
            setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        }
    }

    init {
        decal = Decal.newDecal(size, size, TextureRegion(texture), true)
        decal.setColor(Color.WHITE)
    }

    context(deltaTime: Float)
    abstract fun update()
}