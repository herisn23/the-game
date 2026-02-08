package org.roldy.core.asset

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model

const val scale: Float = 0.1f

interface AssetManagerLoader {
    val assetManager: AssetManager
}

fun <T : Any> T.initialize() =
    when (this) {
        is Model -> apply {
            nodes.forEach {
//                it.scale.scl(scale)
//                it.translation.scl(scale)
            }
        }

        else -> this
    }