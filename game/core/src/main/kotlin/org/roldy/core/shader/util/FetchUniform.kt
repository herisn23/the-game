package org.roldy.core.shader.util

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import kotlin.reflect.KProperty

class FetchUniform(
    private val program: ShaderProgram,
    private val normalize: Boolean = false,
) {
    private var cachedLocation: Int? = null

    operator fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return cachedLocation ?: program.fetchUniformLocation(
            property.name,
            normalize
        ).also { cachedLocation = it }
    }
}

fun ShaderProgram.fetchUniform(normalize: Boolean = false) =
    FetchUniform(this, normalize)