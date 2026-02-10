package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import org.roldy.core.asset.Asset
import org.roldy.core.asset.AssetManagerLoader
import org.roldy.core.shader.util.ShaderUserData

interface EnvironmentAssetManagerLoader : AssetManagerLoader {
    val modelMap: Map<String, Asset<Model>>
}

fun Asset<Model>.simpleFoliage(
    material: Material
): ModelInstance =
    instance {
        it.hasWind = true
    }.apply {
        materials.clear()

        nodes.first().children.forEach { node ->
            node.parts.forEach { part ->
                part.material = material
            }
        }
    }

fun Asset<Model>.tree(
    branchMat: Material,
    leavesMat: Material
): ModelInstance =
    instance {
        it.hasWind = true
    }.apply {
        materials.clear()

        nodes.first().children.forEach { node ->
            node.parts.forEach { part ->
                if (node.id.contains("Branches"))
                    part.material = branchMat
                else
                    part.material = leavesMat
            }
        }
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
    update: Material.(ShaderUserData) -> Unit = {},
): ModelInstance =
    ModelInstance(get()).apply {
        val udata = ShaderUserData()
        userData = udata
        materials.forEach { mat ->
            mat.clear()
            mat.update(udata)
        }
    }