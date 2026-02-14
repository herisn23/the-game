package org.roldy.core.postprocess

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.crashinvaders.vfx.VfxRenderContext
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.shader.util.fetchUniform
import kotlin.random.Random

/**
 * Screen Space Ambient Occlusion (SSAO) effect
 * Provides realistic ambient occlusion based on screen-space depth analysis
 *
 * Usage:
 *   val ssao = SSAO()
 *   ssao.depthTexture = myDepthFBO.colorBufferTexture  // must be set each frame
 *   ssao.setProjectionMatrices(camera.projection)
 *   vfxManager.addEffect(ssao)
 */
class SSAO : ChainVfxEffect {
    private var disabled = false
    private val shader: ShaderProgram = ShaderLoader.ssaoShader

    companion object {
        private const val KERNEL_SIZE = 64
        private const val NOISE_SIZE = 4
    }

    // Configuration
    var radius = 0.5f
    var bias = 0.025f
    var power = 2.0f

    // Must be set externally before rendering
    var depthTexture: Texture? = null

    // Matrices — call setProjectionMatrices() each frame
    private val projection = Matrix4()
    private val invProjection = Matrix4()

    // Generated data
    private val kernel = Array(KERNEL_SIZE) { Vector3() }
    private lateinit var noiseTexture: Texture

    // Uniform locations
    val u_texture0 by shader.fetchUniform()     // scene color (from ping-pong)
    val u_depthTexture by shader.fetchUniform()
    val u_noiseTexture by shader.fetchUniform()
    val u_projection by shader.fetchUniform()
    val u_invProjection by shader.fetchUniform()
    val u_screenSize by shader.fetchUniform()
    val u_radius by shader.fetchUniform()
    val u_bias by shader.fetchUniform()
    val u_power by shader.fetchUniform()
    private val screenQuad: Mesh = createScreenQuad()

    private fun createScreenQuad(): Mesh {
        val mesh = Mesh(
            true, 4, 6,
            VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
            VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        )
        // Fullscreen quad: positions in clip space [-1,1], UVs in [0,1]
        mesh.setVertices(
            floatArrayOf(
                // x,    y,   u,   v
                -1f, -1f, 0f, 0f,
                1f, -1f, 1f, 0f,
                1f, 1f, 1f, 1f,
                -1f, 1f, 0f, 1f
            )
        )
        mesh.setIndices(shortArrayOf(0, 1, 2, 2, 3, 0))
        return mesh
    }

    init {
        generateKernel()
        generateNoiseTexture()
    }

    /**
     * Generate hemisphere sample kernel with samples biased toward the origin
     */
    private fun generateKernel() {
        val rand = Random(1337)
        for (i in 0 until KERNEL_SIZE) {
            // Random point in hemisphere (z >= 0)
            val sample = Vector3(
                rand.nextFloat() * 2f - 1f,
                rand.nextFloat() * 2f - 1f,
                rand.nextFloat() // only positive z = hemisphere
            ).nor()

            // Random length, biased closer to origin
            var scale = i.toFloat() / KERNEL_SIZE.toFloat()
            scale = MathUtils.lerp(0.1f, 1.0f, scale * scale)
            sample.scl(scale)

            kernel[i] = sample
        }
    }

    /**
     * Generate a small tiling noise texture for random kernel rotation
     */
    private fun generateNoiseTexture() {
        val rand = Random(42)
        val pixmap = Pixmap(NOISE_SIZE, NOISE_SIZE, Pixmap.Format.RGBA8888)

        for (x in 0 until NOISE_SIZE) {
            for (y in 0 until NOISE_SIZE) {
                // Random rotation vectors in XY plane, packed to 0-1
                val nx = rand.nextFloat() * 2f - 1f
                val ny = rand.nextFloat() * 2f - 1f
                pixmap.drawPixel(
                    x, y,
                    Color.rgba8888(
                        nx * 0.5f + 0.5f,
                        ny * 0.5f + 0.5f,
                        0f,
                        1f
                    )
                )
            }
        }

        noiseTexture = Texture(pixmap).apply {
            setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
            setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        }
        pixmap.dispose()
    }

    /**
     * Call each frame with your camera's projection matrix
     */
    fun setProjectionMatrices(cameraProjection: Matrix4) {
        projection.set(cameraProjection)
        invProjection.set(cameraProjection).inv()
    }

    override fun render(
        context: VfxRenderContext,
        buffers: VfxPingPongWrapper
    ) {
        val depth = depthTexture ?: return

        // Src = current scene color, dst = where we write the result
        val srcTexture = buffers.srcBuffer.texture
        val dstBuffer = buffers.dstBuffer

        dstBuffer.begin()

        shader.bind()

        // Bind textures
        // Unit 0: scene color
        srcTexture.bind(0)
        shader.setUniformi("u_texture0", 0)

        // Unit 1: depth
        depth.bind(1)
        shader.setUniformi("u_depthTexture", 1)

        // Unit 2: noise
        noiseTexture.bind(2)
        shader.setUniformi("u_noiseTexture", 2)

        // Rebind unit 0 as active (libGDX quirk)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)

        // Upload kernel
        for (i in 0 until KERNEL_SIZE) {
            shader.setUniformf("u_samples[$i]", kernel[i])
        }

        // Upload matrices
        shader.setUniformMatrix("u_projection", projection)
        shader.setUniformMatrix("u_invProjection", invProjection)

        // Upload parameters
        shader.setUniformf(
            "u_screenSize",
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )
        shader.setUniformf("u_radius", radius)
        shader.setUniformf("u_bias", bias)
        shader.setUniformf("u_power", power)

        // Draw fullscreen quad
        screenQuad.render(shader, GL20.GL_TRIANGLES)

        dstBuffer.end()
    }

    override fun isDisabled(): Boolean = disabled

    override fun setDisabled(disabled: Boolean) {
        this.disabled = disabled
    }

    override fun resize(width: Int, height: Int) {
        // Noise scale is computed in shader based on u_screenSize
    }

    override fun update(delta: Float) {}

    override fun rebind() {
        shader.bind()
    }

    override fun dispose() {
        noiseTexture.dispose()
        shader.dispose()
    }
}