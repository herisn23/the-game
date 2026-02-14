package org.roldy.core.asset

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import org.roldy.core.logger


object ShaderLoader {
    val characterFrag by lazy { load("shaders/character.frag.glsl") }

    val terrainVert by lazy { load("shaders/terrain.vert.glsl") }
    val terrainFrag by lazy { load("shaders/terrain.frag.glsl") }

    val skyboxFrag by lazy { loadAsset("shaders/skybox.frag.glsl") }
    val skyboxVert by lazy { loadAsset("shaders/skybox.vert.glsl") }

    val defaultFrag by lazy { load("shaders/default.frag.glsl") }
    val defaultVert by lazy { load("shaders/default.vert.glsl") }

    val foliageFrag by lazy { load("shaders/foliage.frag.glsl") }
    val foliageVert by lazy { load("shaders/foliage.vert.glsl") }


    val depthFrag by lazy { load("shaders/depth.frag.glsl") }
    val depthVert by lazy { load("shaders/depth.vert.glsl") }

    val windSystem by lazy { load("shaders/wind_system.glsl") }

    val ssaoFrag by lazy { loadAsset("shaders/ssao.frag.glsl") }
    val ssaoVert by lazy { loadAsset("shaders/ssao.vert.glsl") }

    val skyboxShader by lazy { createShader(skyboxVert, skyboxFrag) }

    val ssaoShader by lazy { createShader(ssaoVert, ssaoFrag) }
    fun load(name: String) =
        loadAsset(name).readString()

    fun createShader(vert: FileHandle, frag: FileHandle) =
        ShaderProgram(vert, frag).apply {
            if (isCompiled) {
                logger.info("Shader $vert:$frag compiled")
            } else {
                logger.error("Shader $vert:$frag not compiled:\n$log")
            }
        }
}