package org.roldy.core.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import org.roldy.core.logger


object ShaderLoader {
    val characterFrag by lazy { load("shaders/characterHDR.frag.glsl") }
    val ssaoFrag by lazy { loadAsset("shaders/ssao.frag.glsl") }
    val screenspaceVert by lazy { Gdx.files.classpath("gdxvfx/shaders/screenspace.vert") }

    val skyboxFrag by lazy { loadAsset("shaders/skybox.frag.glsl") }
    val skyboxVert by lazy { loadAsset("shaders/skybox.vert.glsl") }
    val skyboxShader by lazy { createShader(skyboxVert, skyboxFrag) }

    fun load(name: String) =
        loadAsset(name).readString()

    fun createShader(vert: FileHandle, frag: FileHandle) =
        ShaderProgram(vert, frag).apply {
            if (isCompiled) {
                logger.info("Shader $vert/$frag compiled")
            } else {
                logger.error("Shader $vert/$frag not compiled")
            }
        }
}