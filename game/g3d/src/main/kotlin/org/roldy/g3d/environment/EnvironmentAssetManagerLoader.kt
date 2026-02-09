package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import org.roldy.core.asset.Asset
import org.roldy.core.asset.AssetManagerLoader
import org.roldy.core.shader.*

interface EnvironmentAssetManagerLoader : AssetManagerLoader {
    val modelMap: Map<String, Asset<Model>>
}


fun Asset<Model>.grass(
    diffuse: Texture,
    normal: Texture,
    color: FoliageColor
): ModelInstance =
    instance {
        it.hasWind = true
        set(TextureAttribute.createNormal(normal))
        set(BooleanAttribute.createLeafFlatColor(true))
        set(BooleanAttribute.createUseNoiseColor(true))
        set(IntAttribute.createCullFace(GL20.GL_NONE))
        updateLeaf(diffuse, color)
    }

fun Asset<Model>.palm(
    leaf: Texture,
    bark: Texture,
    color: FoliageColor
): ModelInstance =
    instance {
        it.hasWind = true

        set(TextureAttribute.createDiffuse(leaf)) // needed for alpha clipping on shadows

        set(BooleanAttribute.createLeafFlatColor(false))
        set(BooleanAttribute.createTrunkFlatColor(false))
        set(BooleanAttribute.createUseNoiseColor(true))

        set(FoliageTextureAttribute.createTrunkTexture(bark))
        set(FoliageColorAttribute.createTrunkBaseColor(Color.WHITE.cpy()))
        set(FoliageColorAttribute.createTrunkNoiseColor(Color.WHITE.cpy()))

        set(FoliageTextureAttribute.createLeafTexture(leaf))
        set(FoliageColorAttribute.createLeafBaseColor(color.base))
        set(FoliageColorAttribute.createLeafNoiseColor(color.noise))
        set(FoliageColorAttribute.createLeafNoiseLargeColor(color.noiseLarge))

        set(IntAttribute.createCullFace(GL20.GL_NONE))

        set(FloatAttribute.createAlphaTest(0.2f))
    }


fun Asset<Model>.tree(
    branchesDiffuse: Texture,
    leavesDiffuse: Texture,
    trunkDiffuse: Texture,
    color: FoliageColor
): ModelInstance =
    instance {
        it.hasWind = true
    }.apply {
        materials.clear()
        val branchMaterial = Material().apply {
            id = "branch"

            set(BooleanAttribute.createTrunkFlatColor(false))
            set(BooleanAttribute.createUseNoiseColor(false))

            updateTrunk(branchesDiffuse)

        }

        val treeMaterial = Material().apply {
            id = "tree"

            set(BooleanAttribute.createTrunkFlatColor(false))
            updateTrunk(trunkDiffuse)

            set(BooleanAttribute.createUseNoiseColor(true))
            set(BooleanAttribute.createLeafFlatColor(true))

            updateLeaf(leavesDiffuse, color)


        }

        nodes.first().children.forEach { node ->
            node.parts.forEach { part ->
                if (node.id.contains("Branches"))
                    part.material = branchMaterial
                else
                    part.material = treeMaterial
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

private fun Material.updateLeaf(
    diffuse: Texture,
    color: FoliageColor
) {
    //diffuse texture for shadows
    set(TextureAttribute.createDiffuse(diffuse))

    set(FoliageTextureAttribute.createLeafTexture(diffuse))
    set(FoliageColorAttribute.createLeafBaseColor(color.base))
    set(FoliageColorAttribute.createLeafNoiseColor(color.noise))
    set(FoliageColorAttribute.createLeafNoiseLargeColor(color.noiseLarge))

    set(IntAttribute.createCullFace(GL20.GL_NONE))

    set(FloatAttribute.createAlphaTest(0.9f))

}

private fun Material.updateTrunk(
    diffuse: Texture,
    color: FoliageColor = FoliageColors.white
) {

    set(TextureAttribute.createDiffuse(diffuse))

    set(FoliageTextureAttribute.createTrunkTexture(diffuse))
    set(FoliageColorAttribute.createTrunkBaseColor(color.base))
    set(FoliageColorAttribute.createTrunkNoiseColor(color.noise))

    set(IntAttribute.createCullFace(GL20.GL_NONE))

    set(FloatAttribute.createAlphaTest(0.9f))
}