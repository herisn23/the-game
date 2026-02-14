package org.roldy.core.shader.util

import com.badlogic.gdx.graphics.g3d.Renderable

object ShaderBuilder {
    fun String.shiftFlag() = append("#define shiftFlag")


    fun String.append(str: String) =
        "$str\n$this"


    private fun String.windFlag() = append("#define windFlag")
    fun String.windFlag(renderable: Renderable) =
        let {
            if (hasWind(renderable)) {
                it.windFlag()
            } else {
                it
            }
        }

    fun hasWind(renderable: Renderable): Boolean =
        (renderable.userData as? ShaderUserData)?.foliage ?: false
}