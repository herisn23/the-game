package org.roldy.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import org.roldy.core.asset.loadAsset


class TerrainShader(
    val name: String
) : Disposable {
    private val albedoTex: Texture = Texture(loadAsset("texture/$name/${name}_Albedo.png"))
    private val aoTex: Texture = Texture(loadAsset("texture/$name/${name}_AO.png"))
    private val heightTex: Texture = Texture(loadAsset("texture/$name/${name}_Height.png"))
    private val maskTex: Texture = Texture(loadAsset("texture/$name/${name}_Mask.png"))
    private val metallicTex: Texture = Texture(loadAsset("texture/$name/${name}_Metallic.png"))
    private val normalTex: Texture = Texture(loadAsset("texture/$name/${name}_Normal.png"))
    private val roughnessTex: Texture = Texture(loadAsset("texture/$name/${name}_Roughness.png"))

    private val pbrShader: ShaderProgram = loadShader(loadAsset("texture/pbr.vert"), loadAsset("texture/pbr.frag"))
    private val debugShader: ShaderProgram? = null
    private val debugMode = 0

    val shader get() = pbrShader
    fun loadShader(vertPath: FileHandle, fragPath: FileHandle) =
        ShaderProgram(vertPath.readString(), fragPath.readString()).apply {
            if (!isCompiled) {
                Gdx.app.error("Shader", "Failed to compile shader:")
                Gdx.app.error("Shader", log)
            } else {
                Gdx.app.log("Shader", "Successfully compiled: $vertPath + $fragPath")
            }
        }

    fun bind() {
        shader.apply {
            albedoTex.bind(0);
            setUniformi("u_texture", 0);

            aoTex.bind(1);
            setUniformi("u_ao", 1);

            heightTex.bind(2);
            setUniformi("u_height", 2);

            maskTex.bind(3);
            setUniformi("u_mask", 3);

            metallicTex.bind(4);
            setUniformi("u_metallic", 4);

            normalTex.bind(5);
            setUniformi("u_normal", 5);

            roughnessTex.bind(6);
            setUniformi("u_roughness", 6);
        }

    }

    fun SpriteBatch.draw(x:Float, y:Float) {
        draw(albedoTex, x, y)
    }

    override fun dispose() {
        pbrShader.dispose()
        albedoTex.dispose()
        aoTex.dispose()
        heightTex.dispose()
        maskTex.dispose()
        metallicTex.dispose()
        normalTex.dispose()
        roughnessTex.dispose()
    }
}