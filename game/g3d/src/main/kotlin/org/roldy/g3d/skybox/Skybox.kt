package org.roldy.g3d.skybox

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import org.roldy.core.CameraRenderable
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.asset.loadAsset
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.system.DayNightSystem


class Skybox(
    val dayNightSystem: DayNightSystem,
) : AutoDisposableAdapter(), CameraRenderable {

    private val day: Cubemap by disposable { createCubeMap("day/FluffballDay") }
    private val night: Cubemap by disposable { createCubeMap("night/CosmicCoolCloud") }
    private var nightRotation = 0f
    private val nightRotationSpeed = 0.02f
    private var blend = 0f
    private var brightness = 0f
    private val dayTint = Vector3()
    private val shader by disposable {
        ShaderLoader.skyboxShader
    }

    private val mesh by disposable {
        val vertices = floatArrayOf(
            -1f, 1f, -1f, -1f, -1f, -1f, 1f, -1f, -1f, 1f, 1f, -1f,
            -1f, -1f, 1f, -1f, -1f, -1f, -1f, 1f, -1f, -1f, 1f, 1f,
            1f, -1f, -1f, 1f, -1f, 1f, 1f, 1f, 1f, 1f, 1f, -1f,
            -1f, -1f, 1f, -1f, 1f, 1f, 1f, 1f, 1f, 1f, -1f, 1f,
            -1f, 1f, -1f, 1f, 1f, -1f, 1f, 1f, 1f, -1f, 1f, 1f,
            -1f, -1f, -1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f, -1f, -1f
        )
        val indices = shortArrayOf(
            0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4, 8, 9, 10, 10, 11, 8,
            12, 13, 14, 14, 15, 12, 16, 17, 18, 18, 19, 16, 20, 21, 22, 22, 23, 20
        )
        Mesh(true, 24, 36, VertexAttribute.Position()).apply {
            setVertices(vertices)
            setIndices(indices)
        }
    }

    context(_: Float, camera: Camera)
    override fun render() {
        updateSkyParameters()

        val view = camera.view.cpy()
        view.`val`[Matrix4.M03] = 0f
        view.`val`[Matrix4.M13] = 0f
        view.`val`[Matrix4.M23] = 0f
        val projView = camera.projection.cpy().mul(view)

        // Enable depth writing and set to far plane
        Gdx.gl.glDepthMask(true)  // Changed: enable depth write
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)  // Added: allow writing at max depth
        Gdx.gl.glDisable(GL20.GL_CULL_FACE)
        shader.bind()

        // Set uniforms
        shader.setUniformMatrix("u_projView", projView)
        shader.setUniformf("u_blend", blend)
        shader.setUniformf("u_brightness", brightness)
        shader.setUniformf("u_dayTint", dayTint)
        shader.setUniformf("u_nightRotation", nightRotation);

        // Bind cubemaps
        day.bind(0);
        shader.setUniformi("u_dayCubemap", 0)

        night.bind(1);
        shader.setUniformi("u_nightCubemap", 1)


        mesh.render(shader, GL20.GL_TRIANGLES)

        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
        Gdx.gl.glDepthFunc(GL20.GL_LESS)  // Restore default
    }

    context(delta: Float)
    private fun updateSkyParameters() {
        // Time mapping:
        // 0.00 - midnight
        // 0.25 - sunrise (6am)
        // 0.50 - noon
        // 0.75 - sunset (6pm)
        // 1.00 - midnight


        // Rotate night sky (only when night is visible)
        // Convert delta to "hours" (24 hour cycle)
        val hoursElapsed: Float = (delta / dayNightSystem.dayDurationSeconds) * 24f
        nightRotation += nightRotationSpeed * hoursElapsed * blend


        // Keep in 0-2π range
        nightRotation %= (Math.PI * 2).toFloat()

        if (dayNightSystem.timeOfDay < 0.20f) {
            // Night (midnight to pre-dawn)
            blend = 1.0f
            brightness = 0.3f
            dayTint.set(1f, 1f, 1f)
        } else if (dayNightSystem.timeOfDay < 0.25f) {
            // Dawn transition (night -> sunrise)
            val t: Float = (dayNightSystem.timeOfDay - 0.20f) / 0.05f // 0 to 1
            blend = 1.0f - t
            brightness = MathUtils.lerp(0.3f, 0.7f, t)
            // Tint goes toward orange/pink
            dayTint.set(
                MathUtils.lerp(1.0f, 1.0f, t),
                MathUtils.lerp(1.0f, 0.6f, t),
                MathUtils.lerp(1.0f, 0.4f, t)
            )
        } else if (dayNightSystem.timeOfDay < 0.30f) {
            // Sunrise to morning
            val t: Float = (dayNightSystem.timeOfDay - 0.25f) / 0.05f
            blend = 0.0f
            brightness = MathUtils.lerp(0.7f, 1.0f, t)
            // Orange tint fades to normal
            dayTint.set(
                1.0f,
                MathUtils.lerp(0.6f, 1.0f, t),
                MathUtils.lerp(0.4f, 1.0f, t)
            )
        } else if (dayNightSystem.timeOfDay < 0.70f) {
            // Full day
            blend = 0.0f
            brightness = 1.0f
            dayTint.set(1f, 1f, 1f)
        } else if (dayNightSystem.timeOfDay < 0.75f) {
            // Afternoon to sunset
            val t: Float = (dayNightSystem.timeOfDay - 0.70f) / 0.05f
            blend = 0.0f
            brightness = MathUtils.lerp(1.0f, 0.8f, t)
            // Tint goes orange/red
            dayTint.set(
                1.0f,
                MathUtils.lerp(1.0f, 0.5f, t),
                MathUtils.lerp(1.0f, 0.3f, t)
            )
        } else if (dayNightSystem.timeOfDay < 0.80f) {
            // Sunset transition (sunset -> night)
            val t: Float = (dayNightSystem.timeOfDay - 0.75f) / 0.05f
            blend = t
            brightness = MathUtils.lerp(0.8f, 0.3f, t)
            // Keep sunset tint while blending to night
            dayTint.set(1.0f, 0.5f, 0.3f)
        } else {
            // Night
            blend = 1.0f
            brightness = 0.3f
            dayTint.set(1f, 1f, 1f)
        }
    }


    private fun createCubeMap(dayNight: String) =
        Cubemap(
            load(dayNight, "Left"),   // +X
            load(dayNight, "Right"),    // -X
            load(dayNight, "Top"),     // +Y
            load(dayNight, "Bottom"),  // -Y
            load(dayNight, "Front"),   // +Z
            load(dayNight, "Back")     // -Z
        )

    private fun load(dayNight: String, side: String) = loadAsset("3d/skybox/$dayNight$side.hdr")
}