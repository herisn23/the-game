package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import org.roldy.core.asset.Asset
import org.roldy.core.biome.BiomeType
import org.roldy.core.configuration.data.MaterialData
import org.roldy.core.configuration.data.ModelInstanceData
import org.roldy.core.configuration.loadModelInstanceConfiguration
import org.roldy.core.logger
import org.roldy.core.shader.attribute.AttributeMapper
import org.roldy.core.shader.attribute.UniformAttribute
import org.roldy.core.shader.uniform.EnvTextureUniform
import org.roldy.core.shader.uniform.UniformMapper

val mLogger by logger("ModelLoader")

object SyntyShaderNames {
    const val FOLIAGE = "Synty/Foliage"
    const val GENERIC = "Synty/Generic_Basic"
    const val TRANSPARENT = "Synty/PolygonShaderTransparent"
    const val POLYGON = "Synty/PolygonShader"
}

fun loadModelInstances(
    textures: Map<String, Asset<Texture>>,
) =
    loadModelInstanceConfiguration(BiomeType.Tropical)
        .run {
            val materials = materials.map { it.toMaterial(textures) }
            instances.mapNotNull {
                it.createInstance(materials, TropicalAssetManager)
            }
        }


private fun MaterialData.toMaterial(
    textures: Map<String, Asset<Texture>>
) =
    Material().run {
        id = materialName
        when (shaderName) {
            SyntyShaderNames.FOLIAGE -> {
                val uniforms = uniforms.mapNotNull {
                    UniformMapper.Foliage.map(it, textures)
                }.associateBy { it.id }

                val tex = uniforms[EnvTextureUniform.leafTexture] as? EnvTextureUniform
                tex?.let {
                    set(TextureAttribute.createDiffuse(it.texture)) // for shadows
                }
                set(UniformAttribute.create(uniforms))
                set(IntAttribute.createCullFace(GL20.GL_NONE))
                set(FloatAttribute.createAlphaTest(0.8f))
            }

            SyntyShaderNames.GENERIC -> {
                uniforms.forEach {
                    AttributeMapper.Generic.map(it, textures)?.let(::set)
                }
            }

            SyntyShaderNames.TRANSPARENT -> {
                uniforms.forEach {
                    AttributeMapper.Transparent.map(it, textures)?.let(::set)
                }
            }
        }
        shaderName to this
    }


private fun ModelInstanceData.createInstance(
    materialsMap: List<Pair<String, Material>>,
    asset: EnvironmentAssetManagerLoader
): EnvModelConfiguration? {

    val model = asset.modelMap[modelName]?.get()
    if (model == null) {
        mLogger.warn("Model not found: $modelName")
        return null
    }

    val collision = asset.collisionMap.keys.find { it.contains(modelName) }?.let {
        asset.collisionMap[it]?.get()
    }
    return EnvModelConfiguration(modelName, materialsMap, model, collision, meshes)
}



