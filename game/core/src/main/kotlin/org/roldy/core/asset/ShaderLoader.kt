package org.roldy.core.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram

object ShaderLoader {
    val characterFrag by lazy { load("shaders/characterHDR.fragment.glsl") }
    val dofFrag by lazy { loadAsset("shaders/dof.fragment.glsl") }
    val dofVert by lazy { loadAsset("shaders/dof.vertex.glsl") }
    val screenspaceVert by lazy { Gdx.files.classpath("gdxvfx/shaders/screenspace.vert") }

    val dofShader by lazy { ShaderProgram(dofVert, dofFrag) }

    fun load(name: String) =
        loadAsset(name).readString()
}