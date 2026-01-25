package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.model.Node
import org.roldy.g3d.pawn.part.*

class PawnConfiguration {
    private val maleModel = PawnModelInstance.Data(
        PawnAssetManager.modelMale.get(),
        listOf(
            PawnAssetManager.modelMaleExt.get(),
            PawnAssetManager.modelMaleExt2.get()
        )
    )
    private val femaleModel = PawnModelInstance.Data(
        PawnAssetManager.modelFemale.get(),
        listOf(
            PawnAssetManager.modelFemaleExt.get(),
            PawnAssetManager.modelFemaleExt2.get()
        )
    )
    private val models = mapOf(
        BodyType.Male to maleModel,
        BodyType.Female to femaleModel,
    )
    private val bodyParts = mapOf(
        BodyType.Male to MaleBody,
        BodyType.Female to FemaleBody,
    )
    private val armorParts = mapOf(
        BodyType.Female to FemaleArmor,
        BodyType.Male to MaleArmor,
    )

    val bodyType = BodyType.Female
    val instance get() = instances.getValue(bodyType)
    val defaultColors = DefaultShaderConfig()
    val body get() = bodyParts.getValue(BodyType.Male)
    val armor get() = armorParts.getValue(BodyType.Female)

    val instances = models.map { (type, data) ->
        type to PawnModelInstance(data, type).apply {
            val body = bodyParts.getValue(type)
            val nakedParts = body.singleParts.map { body[it].first() }
            val modularParts = body.modularParts.filter { it != BodyPart.Beard }.map { body[it].first() }
            setVisibility(nakedParts + modularParts)
        }
    }.toMap()

    fun getShaderConfig(node: Node): ShaderConfig =
        when (node.id) {
            else -> defaultColors
        }
}