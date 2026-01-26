package org.roldy.core.postprocess

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.crashinvaders.vfx.VfxRenderContext
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.crashinvaders.vfx.effects.ShaderVfxEffect
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper
import com.crashinvaders.vfx.gl.VfxGLUtils
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.postprocess.DepthFrameBuffer.DepthTextureWrapper

class DepthOfFieldEffect(
    val depthTexture: () -> DepthTextureWrapper,
) : ShaderVfxEffect(
    VfxGLUtils.compileShader(
        ShaderLoader.screenspaceVert,
        ShaderLoader.dofFrag,
    )
), ChainVfxEffect {
    private var width: Int = 0
    private var height: Int = 0

    // DOF parameters
    var focusDistance = 5.0f    // Distance to focus plane
    var focusRange = 2.0f       // Range around focus that's sharp
    var blurStrength = 3.0f     // Max blur amount
    var nearDistance = 0.1f     // Camera near plane
    var farDistance = 100.0f    // Camera far plane

    init {
        rebind()
    }

    override fun render(context: VfxRenderContext, buffers: VfxPingPongWrapper) {
        render(context, buffers.srcBuffer, buffers.dstBuffer)
    }

    fun render(context: VfxRenderContext, src: VfxFrameBuffer, dst: VfxFrameBuffer) {
        // Bind source texture (scene color)
        src.texture.bind(TEXTURE_HANDLE0)

        // Bind depth texture
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + TEXTURE_HANDLE1)
        depthTexture().bind()
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)

        // Render with shader
        renderShader(context, dst)
    }


    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun rebind() {
        program.bind()
        if (program.hasUniform("u_texture0"))
            program.setUniformi("u_texture0", TEXTURE_HANDLE0)
        if (program.hasUniform("u_depthTexture"))
            program.setUniformi("u_depthTexture", TEXTURE_HANDLE1)
        if (program.hasUniform("u_resolution"))
            program.setUniformf("u_resolution", width.toFloat(), height.toFloat())
        if (program.hasUniform("u_focusDistance"))
            program.setUniformf("u_focusDistance", focusDistance)
        if (program.hasUniform("u_focusRange"))
            program.setUniformf("u_focusRange", focusRange)
        if (program.hasUniform("u_blurStrength"))
            program.setUniformf("u_blurStrength", blurStrength)
        if (program.hasUniform("u_nearFar"))
            program.setUniformf("u_nearFar", nearDistance, farDistance)

        // DEBUG
        println("DOF rebind:")
        println("  focusDistance: $focusDistance")
        println("  focusRange: $focusRange")
        println("  blurStrength: $blurStrength")
        println("  near/far: $nearDistance / $farDistance")
    }
}