package org.roldy.core.asset

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import org.roldy.core.logger


object ShaderLoader {
    val characterFrag by lazy { load("shaders/characterHDR.frag.glsl") }
    val terrainVert by lazy { load("shaders/terrain.vert.glsl") }
    val terrainFrag by lazy { load("shaders/terrain.frag.glsl") }
    val skyboxFrag by lazy { loadAsset("shaders/skybox.frag.glsl") }
    val skyboxVert by lazy { loadAsset("shaders/skybox.vert.glsl") }
    val envGenericFrag by lazy { load("shaders/env_generic.frag.glsl") }
    val envGenericVert by lazy { load("shaders/env_generic.vert.glsl") }

    val defaultFrag by lazy { load("shaders/default.frag.glsl") }
    val defaultVert by lazy { load("shaders/default.vert.glsl") }

    val foliageFrag by lazy { load("shaders/foliage.fragment.glsl") }
    val foliageVert by lazy { load("shaders/foliage.vertex.glsl") }

    val skyboxShader by lazy { createShader(skyboxVert, skyboxFrag) }
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