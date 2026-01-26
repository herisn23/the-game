package org.roldy.core.postprocess

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.*
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.utils.invoke

class PostProcessing(
    camera: Camera,
    private var width: () -> Int = { Gdx.graphics.backBufferWidth },
    private var height: () -> Int = { Gdx.graphics.backBufferHeight }
) : AutoDisposableAdapter() {
    private var sceneFBO = DepthFrameBuffer(width(), height())
    private val spriteBatch by disposable { SpriteBatch() }

    val bloom = BloomEffect().apply {
        threshold = 0.6f
        bloomIntensity = 1.5f
        bloomSaturation = 1f
        blurPasses = 2
    }
    val filmGrain = FilmGrainEffect()
    val vignette = VignettingEffect(true).apply {
        setIntensity(0.5f) // Lower = subtle (try 0.3 - 0.7)
        setSaturation(0.8f) // Color saturation
        setSaturationMul(1f) // Better color preservation
    }
    val antialiasing = FxaaEffect().apply {

    }
    val gaussianBlur = GaussianBlurEffect().apply {
        setAmount(1f)     // Blur strength (1-10, default ~2)
        setPasses(4)      // Number of blur passes (1-4, more = smoother but slower)
        setType(GaussianBlurEffect.BlurType.Gaussian5x5) // Blur kernel size
    }
    val dof = DepthOfFieldEffect { sceneFBO.depthTexture }.apply {
        focusDistance = 50.0f
        focusRange = 20.0f     // Wider range = more gradual transition
        blurStrength = 2.0f
        nearDistance = camera.near
        farDistance = camera.far
        rebind()
    }
    val radialBlur = RadialBlurEffect(100).apply {
        setOrigin(0.5f, 0.5f)  // Center point (0-1, normalized screen coords)
        setZoom(1f)          // Blur strength (0.0-1.0, default: 0.1)
    }
    val radialDistortion = RadialDistortionEffect().apply {
        setDistortion(0.3f)    // Distortion amount (-1.0 to 1.0)
        setZoom(1.0f)          // Zoom level (0.5-2.0)
    }
    val vfxManager by disposable {
        VfxManager(Pixmap.Format.RGBA8888, width(), height()).apply {
            addEffect(dof)
//            addEffect(radialDistortion)
            addEffect(bloom)
            addEffect(filmGrain)
            addEffect(vignette)
            addEffect(antialiasing)
        }
    }

    fun resize(width: Int, height: Int) {
        vfxManager.resize(width, height)
        sceneFBO.dispose()
        sceneFBO = DepthFrameBuffer(width(), height())
    }

    var enabled = true

    fun toggle() {
        enabled = !enabled
    }

    operator fun invoke(render: () -> Unit) {
        val width = width()
        val height = height()
        Gdx.gl.glClearColor(1f, 1f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl.glViewport(0, 0, width, height)
        if (enabled) {
            sceneFBO.begin()
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
            Gdx.gl.glDepthFunc(GL20.GL_LEQUAL) // Ensure correct depth function
            Gdx.gl.glDepthMask(true)
            Gdx.gl.glDisable(GL20.GL_BLEND)
            render()
            sceneFBO.end()


            // Clean up internal buffers, as we don't need any information from the last render.
            vfxManager.cleanUpBuffers()

            // Begin capturing
            vfxManager.beginInputCapture()
            // Disable depth test for 2D post-processing
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            spriteBatch {
                projectionMatrix.setToOrtho2D(
                    0f, 0f,
                    width.toFloat(),
                    height.toFloat()
                )
                draw(
                    sceneFBO.colorTexture,
                    0f, 0f,
                    width.toFloat(), height.toFloat(),
                    0f, 0f, 1f, 1f
                )
            }

            // End capturing and apply effects
            vfxManager.endInputCapture()

            // Apply all effects and render to screen
            vfxManager.applyEffects()
            vfxManager.renderToScreen()
        } else {
            // No post-processing - render directly
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
            Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)
            render()
        }
    }
}