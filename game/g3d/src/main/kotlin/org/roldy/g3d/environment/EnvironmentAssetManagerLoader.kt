package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import org.roldy.core.asset.Asset
import org.roldy.core.asset.AssetManagerLoader
import org.roldy.core.shader.FoliageColor
import org.roldy.core.shader.FoliageColorAttribute

interface EnvironmentAssetManagerLoader : AssetManagerLoader {
    val modelMap: Map<String, Asset<Model>>
}


fun Asset<Model>.foliage(
    diffuse: Texture,
    normal: Texture,
    color: FoliageColor
): ModelInstance =
    instance {
        set(TextureAttribute.createDiffuse(diffuse))
        set(TextureAttribute.createNormal(normal))
        set(ColorAttribute.createDiffuse(Color.WHITE))
        set(FoliageColorAttribute.createBaseColor(color.base))
        set(FoliageColorAttribute.createNoiseColor(color.noise))
        set(FoliageColorAttribute.createNoiseLargeColor(color.noiseLarge))
        set(IntAttribute.createCullFace(GL20.GL_NONE))
    }

fun Asset<Model>.property(
    diffuse: Texture,
    emissive: Texture
): ModelInstance =
    instance {
        set(TextureAttribute.createDiffuse(diffuse))
        set(ColorAttribute.createDiffuse(1f, 1f, 1f, 1f))
        set(TextureAttribute.createEmissive(emissive))
        set(ColorAttribute.createEmissive(0f, 0f, 0f, 1f))
    }


private fun Asset<Model>.instance(
    update: Material.() -> Unit = {},
): ModelInstance =
    ModelInstance(get()).apply {
        materials.forEach { mat ->
            mat.clear()
            mat.update()
        }
    }