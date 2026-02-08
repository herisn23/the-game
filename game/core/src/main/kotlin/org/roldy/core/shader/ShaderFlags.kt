package org.roldy.core.shader

import com.badlogic.gdx.graphics.g3d.Renderable

object ShaderFlags {
    fun String.shiftFlag() = run {
        """
                #define shiftFlag
                $this
            """.trimIndent()
    }

    private fun String.windFlag() = run {
        """
                #define windFlag
                $this
            """.trimIndent()
    }

    fun String.windFlag(renderable: Renderable) =
        let {
            val userData = renderable.userData as? ShaderUserData
            val hasWind = userData?.hasWind ?: false
            if (hasWind) {
                it.windFlag()
            } else {
                it
            }
        }
}