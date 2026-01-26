package org.roldy.core.postprocess

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.Disposable

class DepthFrameBuffer(
    val width: Int,
    val height: Int
) : Disposable {

    private var framebufferHandle = 0
    private var depthTextureHandle = 0

    // Create color texture
    val colorTexture: Texture = Texture(width, height, Pixmap.Format.RGBA8888).apply {
        setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge)
    }
    val depthTexture: DepthTextureWrapper

    init {

        // Create depth texture
        depthTextureHandle = createDepthTexture(width, height)
        depthTexture = DepthTextureWrapper(depthTextureHandle, width, height)

        // Build FBO
        build()
    }

    private fun createDepthTexture(width: Int, height: Int): Int {
        val handle = Gdx.gl.glGenTexture()

        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, handle)

        // Create empty depth texture
        Gdx.gl.glTexImage2D(
            GL20.GL_TEXTURE_2D,
            0,
            GL20.GL_DEPTH_COMPONENT,
            width, height,
            0,
            GL20.GL_DEPTH_COMPONENT,
            GL20.GL_UNSIGNED_SHORT,
            null
        )

        // Set filtering
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST)
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST)
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE)
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE)

        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0)

        return handle
    }

    private fun build() {
        val handle = BufferUtils.newIntBuffer(1)

        // Create framebuffer
        Gdx.gl.glGenFramebuffers(1, handle)
        framebufferHandle = handle.get(0)
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, framebufferHandle)

        // Attach color texture
        Gdx.gl.glFramebufferTexture2D(
            GL20.GL_FRAMEBUFFER,
            GL20.GL_COLOR_ATTACHMENT0,
            GL20.GL_TEXTURE_2D,
            colorTexture.textureObjectHandle,
            0
        )

        // Attach depth texture
        Gdx.gl.glFramebufferTexture2D(
            GL20.GL_FRAMEBUFFER,
            GL20.GL_DEPTH_ATTACHMENT,
            GL20.GL_TEXTURE_2D,
            depthTextureHandle,
            0
        )

        // Check FBO status
        val status = Gdx.gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER)
        if (status != GL20.GL_FRAMEBUFFER_COMPLETE) {
            throw RuntimeException("DepthFrameBuffer incomplete: $status")
        }

        // Unbind
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0)
    }

    fun begin() {
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, framebufferHandle)
    }

    fun end() {
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0)
    }

    override fun dispose() {
        colorTexture.dispose()

        // Delete depth texture
        val handle = BufferUtils.newIntBuffer(1)
        handle.put(depthTextureHandle)
        handle.flip()
        Gdx.gl.glDeleteTextures(1, handle)

        // Delete framebuffer
        handle.clear()
        handle.put(framebufferHandle)
        handle.flip()
        Gdx.gl.glDeleteFramebuffers(1, handle)
    }

    // Wrapper class for depth texture
    class DepthTextureWrapper(
        private val handle: Int,
        val width: Int,
        val height: Int
    ) {
        fun bind() {
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, handle)
        }

        fun bind(unit: Int) {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + unit)
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, handle)
        }

        val textureObjectHandle: Int
            get() = handle
    }
}