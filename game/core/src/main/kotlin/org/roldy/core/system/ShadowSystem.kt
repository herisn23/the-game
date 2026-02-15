package org.roldy.core.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight
import com.badlogic.gdx.math.Vector3
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.shader.shiftingDepthShaderProvider

class ShadowSystem(
    private val offsetProvider: OffsetProvider,
    private val camera: Camera,
    shadowQuality: Int = Quality.HIGH,
    shadowDistance: Float = 50f,
    private val windSystem: WindSystem
) : AutoDisposableAdapter() {
    object Quality {
        const val LOW = 1
        const val MEDIUM = 4
        const val HIGH = 8
        const val ULTRA_HIGH = 16
    }

    val gridSize = 1f  // ← Was 1f, now 0.01f
    val shadowMapSize: Int = 1024 * shadowQuality

    val minDistance = 50f
    val maxDistance = 500f


    val sunLight: DirectionalShadowLight by disposable {
        DirectionalShadowLight(
            shadowMapSize, shadowMapSize,
            shadowDistance * 2f, shadowDistance * 2f,
            0.01f, shadowDistance * 3f  // ← Near plane: 1f → 0.01f for scaled models
        )
    }

    val moonLight: DirectionalLight = DirectionalLight().apply {
        // Start dark, DayNightSystem will control color/intensity
        set(0f, 0f, 0f, 0.5f, 1f, 0.3f) // color=black (off), direction opposite sun
    }

    fun updateShadowDistance(factor: Float) {
        val distance = (maxDistance * factor).coerceIn(minDistance, maxDistance)
        sunLight.camera.viewportWidth = distance
        sunLight.camera.viewportHeight = distance
    }

    private val shadowCenter = Vector3()

    // Environment with shadows
    val environment: Environment

    // Separate batch for shadow depth pass
    private val shadowBatch: ModelBatch by disposable {
        ModelBatch(
            shiftingDepthShaderProvider(offsetProvider, windSystem)
        )
    }

    init {
        sunLight.set(1f, 1f, 1f, -0.5f, -1f, -0.3f)
        sunLight.depthMap.texture.setFilter(
            Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest
        )

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(sunLight)
        environment.add(moonLight)  // ADD moonLight to environment
        environment.shadowMap = sunLight
    }

    fun begin() {
        shadowCenter.set(camera.position)

        // Snap to grid scaled for 0.01 world

        shadowCenter.x = (shadowCenter.x / gridSize).toInt() * gridSize
        shadowCenter.z = (shadowCenter.z / gridSize).toInt() * gridSize

        sunLight.begin(shadowCenter, sunLight.direction)
        shadowBatch.begin(sunLight.camera)
    }

    operator fun invoke(
        renderShadows: ShadowSystem.() -> Unit,
        renderScene: context (Environment) () -> Unit
    ) {
        begin()
        renderShadows()
        end()


        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0)

        context(environment) {
            renderScene()
        }
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
        sunLight.end()
    }
}