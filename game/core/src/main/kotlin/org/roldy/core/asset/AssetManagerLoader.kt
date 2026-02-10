package org.roldy.core.asset

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model

const val modelScale: Float = 0.01f

interface AssetManagerLoader {
    val assetManager: AssetManager
}


interface TextureAssetManagerLoader : AssetManagerLoader {
    val textureMap: Map<String, Asset<Texture>>

    val params: AssetLoaderParameters<Texture>
        get() = TextureLoader.TextureParameter().apply {
            genMipMaps = true
            minFilter = Texture.TextureFilter.MipMapLinearNearest
            magFilter = Texture.TextureFilter.Linear
            wrapU = Texture.TextureWrap.Repeat
            wrapV = Texture.TextureWrap.Repeat
        }

}

fun <T : Any> T.initialize() =
    when (this) {
        is Model -> apply {
            nodes.forEach { node ->
                // Scale position/translation only
                node.scale.scl(modelScale)
                node.translation.scl(modelScale)
                node.localTransform
                    .setToTranslation(node.translation)
                    .rotate(node.rotation)
                    .scale(node.scale.x, node.scale.y, node.scale.z) // Keep original scale
            }
            calculateTransforms()
        }

        else -> this
    }