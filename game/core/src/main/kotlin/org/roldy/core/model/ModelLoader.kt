package org.roldy.core.model

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import org.roldy.core.asset.Asset
import org.roldy.core.biome.BiomeType
import org.roldy.core.configuration.data.MaterialData
import org.roldy.core.configuration.loadModelInstanceConfiguration
import org.roldy.core.shader.UniformAttribute
import org.roldy.core.shader.uniform.EnvTextureUniform
import org.roldy.core.shader.uniform.UniformMapper

object SyntyShaderNames {
    const val FOLIAGE = "Synty/Foliage"
}

fun loadModelInstances(
    textures: Map<String, Asset<Texture>>
) =
    loadModelInstanceConfiguration(BiomeType.Tropical)
        .run {
            materials.map { it.toMaterial(textures) }
        }


fun MaterialData.toMaterial(
    textures: Map<String, Asset<Texture>>
) =
    Material().apply {
        id = materialName
        when (shaderName) {
            SyntyShaderNames.FOLIAGE -> {
                val uniforms = uniforms.mapNotNull {
                    UniformMapper.Foliage.map(it, textures)?.let {
                        it.id to it
                    }
                }.toMap()

                val tex = uniforms[EnvTextureUniform.leafTexture] as? EnvTextureUniform
                tex?.let {
                    set(TextureAttribute.createDiffuse(it.texture)) // for shadows
                }
                set(UniformAttribute.create(uniforms))
                set(IntAttribute.createCullFace(GL20.GL_NONE))
                set(FloatAttribute.createAlphaTest(0.5f))
            }
        }

    }