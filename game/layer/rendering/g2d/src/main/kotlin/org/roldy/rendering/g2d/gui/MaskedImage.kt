package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class MaskedImage(
    private val drawable: Drawable,
    var color: Color = Color(1f, 1f, 1f, 1f),
    val mask: Boolean = false // First layer uses normal blending
) : BaseDrawable() {

    private val shader = ShaderProgram(
        """
        attribute vec4 a_position;
        attribute vec4 a_color;
        attribute vec2 a_texCoord0;
        uniform mat4 u_projTrans;
        varying vec4 v_color;
        varying vec2 v_texCoords;
        
        void main() {
            v_color = a_color;
            v_texCoords = a_texCoord0;
            gl_Position = u_projTrans * a_position;
        }
        """,
        """
        #ifdef GL_ES
        precision mediump float;
        #endif
        
        varying vec4 v_color;
        varying vec2 v_texCoords;
        uniform sampler2D u_texture;
        
        void main() {
            vec4 texColor = texture2D(u_texture, v_texCoords);
            vec4 color = texColor * v_color;
            
            // Unity overlay: lerp between gray and color
            vec4 gray = vec4(0.5, 0.5, 0.5, 0.5);
            gl_FragColor = mix(gray, color, color.a);
        }
        """
    )

    init {
        if (!shader.isCompiled) {
            Gdx.app.error("OverlayShader", "Error: ${shader.log}")
        }

        minWidth = drawable.minWidth
        minHeight = drawable.minHeight
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        batch.flush()

        val oldSrcFunc = batch.blendSrcFunc
        val oldDstFunc = batch.blendDstFunc
        val oldShader = batch.shader
        val oldColor = batch.color.cpy()

        if (!mask) {
            // First layer uses normal alpha blending
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        } else {
            // Subsequent layers use multiplicative blending
            batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR)
        }

        batch.shader = shader
        batch.setColor(color)
        drawable.draw(batch, x, y, width, height)

        batch.color = oldColor
        batch.flush()
        batch.setBlendFunction(oldSrcFunc, oldDstFunc)
        batch.shader = oldShader
    }

    fun dispose() {
        shader.dispose()
    }
}

@DrawableDsl
fun mask(
    content: Drawable,
    color: Color = Color.WHITE,
    init: @DrawableDsl MaskedImage.() -> Unit = {}
): MaskedImage =
    MaskedImage(content, color, true).apply {
        init()
    }

@DrawableDsl
fun StackDrawable.maskable(
    content: Drawable,
    color: Color = Color.WHITE,
    init: @DrawableDsl MaskedImage.() -> Unit = {}
): MaskedImage =
    MaskedImage(content, color, false).apply {
        init()
        add(this)
    }
@DrawableDsl
@JvmName("stackmask")
fun StackDrawable.mask(
    content: Drawable,
    color: Color = Color.WHITE,
    init: @DrawableDsl MaskedImage.() -> Unit = {}
): MaskedImage =
    MaskedImage(content, color, true).apply {
        init()
        add(this)
    }