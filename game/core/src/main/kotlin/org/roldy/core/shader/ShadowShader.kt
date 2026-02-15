package org.roldy.core.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.math.Vector3
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.shader.util.ShaderBuilder
import org.roldy.core.shader.util.ShiftingManager
import org.roldy.core.shader.util.WindShaderManager
import org.roldy.core.shader.util.shaderProvider
import org.roldy.core.system.WindSystem

class ShadowShader(
    renderable: Renderable,
    windSystem: WindSystem,
    offsetProvider: OffsetProvider = object : OffsetProvider {
        override val shiftOffset = Vector3()
    }
) : DepthShader(
    renderable, DepthConfig().apply {
        with(ShaderBuilder) {
            hasWind = hasWind(renderable)
            vertexShader = ShaderLoader.depthVert.append(ShaderLoader.windSystem).shiftFlag().windFlag(renderable)
            fragmentShader = ShaderLoader.depthFrag
        }
    }
) {

    class DepthConfig : Config() {
        var hasWind: Boolean = false
    }

    val windManager = WindShaderManager(windSystem, program)
    val shiftingManager = ShiftingManager(program, offsetProvider)


    override fun begin(camera: Camera, context: RenderContext) {
        super.begin(camera, context)
        // Enable culling for shadow pass
//        context.setCullFace(GL20.GL_FRONT)
    }

    override fun render(renderable: Renderable) {
        shiftingManager.shift(renderable)

        // Set wind uniforms
        windManager.render(renderable)
        super.render(renderable)
    }

    override fun end() {
        super.end()
        // Reset texture unit after terrain rendering
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
    }

    override fun canRender(renderable: Renderable): Boolean {
        return super.canRender(renderable) && ShaderBuilder.hasWind(renderable) == (config as DepthConfig).hasWind
    }
}


fun shiftingDepthShaderProvider(offsetProvider: OffsetProvider, windSystem: WindSystem) =
    shaderProvider {
        ShadowShader(it, windSystem, offsetProvider = offsetProvider)
    }