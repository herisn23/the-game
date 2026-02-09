package org.roldy.core.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
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
    shadowDistance: Float = 100f,
    private val windSystem: WindAttributes
) : AutoDisposableAdapter() {
    object Quality {
        const val LOW = 1
        const val MEDIUM = 4
        const val HIGH = 8
        const val ULTRA_HIGH = 16
    }

    val gridSize = 1f  // ← Was 1f, now 0.01f
    val shadowMapSize: Int = 1024 * shadowQuality

    val minDistance = 100f
    val maxDistance = 500f



    val shadowLight: DirectionalShadowLight by disposable {
        DirectionalShadowLight(
            shadowMapSize, shadowMapSize,
            shadowDistance * 2f, shadowDistance * 2f,
            1f, shadowDistance * 5f  // ← Near plane: 1f → 0.01f for scaled models
        )
    }

    fun updateShadowDistance(factor: Float) {
//        val distance = (maxDistance * factor).coerceIn(minDistance, maxDistance)
//        shadowLight.camera.viewportWidth = distance
//        shadowLight.camera.viewportHeight = distance
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
        shadowCenter.set(camera.position)

        shadowCenter.x = (shadowCenter.x / gridSize).toInt() * gridSize
        shadowCenter.z = (shadowCenter.z / gridSize).toInt() * gridSize

        // Use the LIGHT's direction, not camera direction!
        shadowLight.begin(shadowCenter, shadowLight.direction)  // ← Changed!
        shadowBatch.begin(shadowLight.camera)
    }

    operator fun invoke(render: ShadowSystem.() -> Unit) {

        begin()
        render()
        end()

        // Unbind shadow framebuffer texture
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0)  // ← Add this
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

    fun debugRenderShadowMap(batch: SpriteBatch, x: Float = 0f, y: Float = 0f, size: Float = 256f) {
        val texture = shadowLight.frameBuffer.colorBufferTexture  // ← Use this instead
        batch.begin()
        batch.draw(texture, x, y, size, size)
        batch.end()
    }
}