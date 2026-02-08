package org.roldy.core.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight
import com.badlogic.gdx.math.Vector3
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.shader.shiftingDepthShaderProvider

class ShadowSystem(
    private val offsetProvider: OffsetProvider,
    private val camera: Camera,
    private val shadowQuality: Int = Quality.ULTRA_HIGH,
    private val shadowDistance: Float = 2000f,
    private val windSystem: WindAttributes
) : AutoDisposableAdapter() {
    object Quality {
        const val LOW = 1
        const val MEDIUM = 4
        const val HIGH = 8
        const val ULTRA_HIGH = 16
    }

    val shadowMapSize: Int = 1024 * shadowQuality      // Resolution of shadow map (1024, 2048, 4096)

    // Shadow light replaces regular DirectionalLight
    // Create shadow-casting directional light
    val shadowLight: DirectionalShadowLight by disposable {
        DirectionalShadowLight(
            shadowMapSize, shadowMapSize,
            shadowDistance * 2f, shadowDistance * 2f,  // Viewport covers 2x distance (both sides)
            1f, shadowDistance * 3f                     // Far enough to capture tall objects
        )
    }

    private val shadowCenter = Vector3()

    // Environment with shadows
    val environment: Environment

    // Separate batch for shadow depth pass
    private val shadowBatch: ModelBatch by disposable {
        ModelBatch(shiftingDepthShaderProvider(offsetProvider, windSystem))
    }

    init {
        shadowLight.set(1f, 1f, 1f, -0.5f, -1f, -0.3f) // color and direction

        // Setup environment
        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(shadowLight)
        environment.shadowMap = shadowLight
    }

    fun begin() {
        // Shadow follows camera
        shadowCenter.set(camera.position)

        // Optional: Snap to grid to reduce shadow swimming/flickering
        val gridSize = 1f
        shadowCenter.x = (shadowCenter.x / gridSize).toInt() * gridSize
        shadowCenter.z = (shadowCenter.z / gridSize).toInt() * gridSize

        shadowLight.begin(shadowCenter, shadowLight.direction)
        shadowBatch.begin(shadowLight.camera)
    }

    operator fun invoke(render: ShadowSystem.() -> Unit) {
        begin()
        render()
        end()

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0)
    }

    fun render(instance: ModelInstance) {
        shadowBatch.render(instance)
    }

    fun render(instances: Iterable<ModelInstance>) {
        instances.forEach {
            shadowBatch.render(it)
        }
    }

    fun end() {
        shadowBatch.end()
        shadowLight.end()
    }
}