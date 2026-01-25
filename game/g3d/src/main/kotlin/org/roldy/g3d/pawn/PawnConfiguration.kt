package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.model.NodePart
import org.roldy.core.utils.hex
import org.roldy.g3d.pawn.utils.copyAnimation

class PawnConfiguration(
    model: Model
) {
    private val testParts = listOf(
        "Chr_Hips_Male_00",
        "Chr_HandLeft_Male_00",
        "Chr_HandRight_Male_00",
        "Chr_LegLeft_Male_00",
        "Chr_LegRight_Male_00",
        "Chr_ArmLowerLeft_Male_00",
        "Chr_ArmLowerRight_Male_11",
//            "Chr_ArmLowerRight_Male_00",
        "Chr_ArmUpperLeft_Male_00",
        "Chr_ArmUpperRight_Male_00",
        "Chr_Torso_Male_00",
//        "Chr_FacialHair_Male_01",
//        "Chr_Eyebrow_Male_01",
        "Chr_Head_Male_19",
//        "Chr_HeadCoverings_No_Hair_09"
    )
    private val nodeParts: List<Pair<Node, List<NodePart>>>
    val allNodes: List<Node>
    val instance: ModelInstance = ModelInstance(model.apply {
        PawnAnimations.all.forEach { (id, anim) ->
            animations.add(copyAnimation(anim.model.get().animations.first(), id))
        }
    }).apply {
        allNodes = nodes.collect()
        nodeParts = allNodes.map {
            it to it.parts.toList()
        }
        setVisibility(testParts)
    }


    private fun Iterable<Node>.collect(): List<Node> =
        flatMap {
            it.children.collect() + listOf(it)
        }


    fun setVisibility(visibleParts: List<String>) {
        nodeParts.forEach { (node, parts) ->
            parts.forEach {
                it.enabled = visibleParts.contains(node.id)
            }
        }
    }

    var colorPrimary = hex("7B6A5A")
    var colorSecondary = hex("BCBCBC")
    var colorLeatherPrimary = hex("4F3729")
    var colorLeatherSecondary = hex("8E9F99")
    var colorMetalPrimary = hex("5F5447")
    var colorMetalSecondary = hex("2A3845")
    var colorMetalDark = hex("66675B")
    var colorHair = hex("623C0D")
    var colorSkin = hex("D1A275")
    var colorStubble = hex("A89276")
    var colorScar = hex("B28B66")
    var colorBodyArt = hex("299F2A")
    var colorEyes = hex("000000")
    var bodyArtAmount = 1f
}