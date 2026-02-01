package org.roldy.g3d.environment

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import org.roldy.core.asset.Asset
import org.roldy.core.asset.AssetManagerLoader
import org.roldy.core.asset.SyntyModelLoader

interface EnvironmentAssetManagerLoader : AssetManagerLoader {
    val modelMap: Map<String, Asset<Model>>
}

fun AssetManager.configure() {
    val loader = SyntyModelLoader(fileHandleResolver)
    setLoader(Model::class.java, ".g3db", loader)
}

fun TropicalAssetManager.TropicalAsset<Model>.instance(
    diffuse: Texture,
    emissive: Texture,
): ModelInstance =
    ModelInstance(get()).apply {
        materials.forEach { mat ->
            mat.clear()
            mat.set(ColorAttribute.createAmbient(1f, 1f, 1f, 1f))
            mat.set(TextureAttribute.createDiffuse(diffuse))
            mat.set(ColorAttribute.createDiffuse(1f, 1f, 1f, 1f))
            mat.set(TextureAttribute.createEmissive(emissive))
            mat.set(ColorAttribute.createEmissive(0f, 0f, 0f, 1f))
        }
    }