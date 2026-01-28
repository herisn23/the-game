package org.roldy.g3d.terrain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import org.roldy.core.asset.ShaderLoader

class TerrainShader(
    private val snowTexture: Texture,
    private val rockTexture: Texture,
    private val grassTexture: Texture,
    private val sandTexture: Texture,
    private val dirtTexture: Texture
) : Shader {

    private val program: ShaderProgram = ShaderLoader.terrainShader
    private val lightDir = Vector3(-1f, -0.8f, -0.2f).nor()

    override fun init() {
        if (!program.isCompiled) {
            throw RuntimeException("Terrain shader error: ${program.log}")
        }
    }

    override fun compareTo(other: Shader): Int = 0
    override fun canRender(instance: Renderable): Boolean = true

    override fun begin(camera: Camera, context: RenderContext) {
        program.bind()
        program.setUniformMatrix("u_projViewTrans", camera.combined)
        program.setUniformf("u_lightDir", lightDir)
        program.setUniformf("u_ambientLight", 0.3f)

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        grassTexture.bind()
        program.setUniformi("u_tex1", 0)

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
        rockTexture.bind()
        program.setUniformi("u_tex2", 1)

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE2)
        sandTexture.bind()
        program.setUniformi("u_tex3", 2)

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE3)
        snowTexture.bind()
        program.setUniformi("u_tex4", 3)

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)

        context.setDepthTest(GL20.GL_LEQUAL)
        context.setCullFace(GL20.GL_BACK)
    }

    override fun render(renderable: Renderable) {
        program.setUniformMatrix("u_worldTrans", renderable.worldTransform)
        renderable.meshPart.render(program)
    }

    override fun end() {
        // Reset texture unit after terrain rendering
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
    }

    override fun dispose() {
        program.dispose()
    }
}

class TerrainShaderProvider(
    private val grassRegion: TextureRegion,
    private val rockRegion: TextureRegion,
    private val sandRegion: TextureRegion,
    private val snowRegion: TextureRegion,
    private val dirtRegion: TextureRegion
) : BaseShaderProvider() {
    override fun createShader(renderable: Renderable): Shader {
        return TerrainShader(
            snowRegion.texture,
            rockRegion.texture,
            grassRegion.texture,
            sandRegion.texture,
            dirtRegion.texture
        )
    }
}