package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import org.roldy.core.utils.sequencer
import org.roldy.g3d.pawn.part.*

class PawnManager(
    val bodyType: BodyType = BodyType.Female
) {

    val beardIndex = 0
    val hairIndex = 0
    val headIndex = 0
    private val modelData by lazy {
        when (bodyType) {
            BodyType.Male -> PawnModelInstance.Data(
                PawnAssetManager.modelMale.get(),
                listOf(
                    PawnAssetManager.modelMaleExt.get(),
                    PawnAssetManager.modelMaleExt2.get()
                )
            )

            BodyType.Female -> PawnModelInstance.Data(
                PawnAssetManager.modelFemale.get(),
                listOf(
                    PawnAssetManager.modelFemaleExt.get(),
                    PawnAssetManager.modelFemaleExt2.get()
                )
            )
        }
    }
    private val body by lazy {
        when (bodyType) {
            BodyType.Male -> MaleBody
            BodyType.Female -> FemaleBody
        }
    }
    private val armor by lazy {
        when (bodyType) {
            BodyType.Male -> MaleArmor
            BodyType.Female -> FemaleArmor
        }
    }

    private val heads by lazy { body[BodyPart.Head] }
    private val hairs by lazy { body[BodyPart.Hair] }
    private val beards by lazy { body[BodyPart.Beard] }
    private val naked by lazy { body.singleParts.map { body[it].first() } }

    private val head get() = heads[headIndex]
    private val hair get() = hairs[hairIndex]
    private val beard get() = beards.getOrNull(beardIndex)

    val defaultColors = DefaultShaderConfig()

    val instance by lazy {
        PawnModelInstance(modelData, bodyType).apply {
            //set defaults
            val modularParts = body.modularParts.mapNotNull { body[it].firstOrNull() }
            setVisibility(modularParts + naked)
        }
    }

    val animationController = AnimationController(instance).apply {
        setAnimation(instance.animations.first().id, -1)
    }


    fun setArmor(
        selection: Map<ArmorPart, String>
    ) {
        val pieces = run {
            ArmorPart.entries.flatMap { armorPart ->
                selection[armorPart]?.let {
                    listOf(it)
                } ?: run {
                    //when armor part is not appearing, replace it with body part
                    getBodyParts(armorToBody.getValue(armorPart))
                }
            } + listOf(head)//head is always appearing
        }.toSet().toList()//remove duplicities
        instance.setVisibility(pieces)
    }

    private fun getBodyParts(parts: List<BodyPart>): List<String> =
        parts.flatMap {
            when (it) {
                BodyPart.Head -> listOf(head)
                BodyPart.Hair -> listOf(hair)
                BodyPart.Beard -> listOfNotNull(beard)
                else -> body[it]
            }
        }

    fun getShaderConfig(node: Node): ShaderConfig =
        when (node.id) {
            else -> defaultColors
        }

    val setKeys by sequencer {
        armor.sets.keys.toList()
    }

    context(delta: Float)
    fun update() {
        animationController.update(delta)
    }

    //test functions
    fun cycleSets() {
        val parts = armor.sets.getValue(setKeys.next().also(::println))
        setArmor(parts)
    }
}