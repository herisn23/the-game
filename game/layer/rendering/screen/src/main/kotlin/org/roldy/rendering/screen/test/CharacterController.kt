package org.roldy.rendering.screen.test

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.model.Animation
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.roldy.core.utils.hex
import com.badlogic.gdx.utils.Array as GdxArray

class CharacterController(
    val model: Model,
    val maskTextures: MaskTextures,
    val animations: Map<String, Animation>
) {
    val parts = listOf(
        "Root",
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

    fun Iterable<Node>.findNode(id: String): Node? =
        firstNotNullOfOrNull {
            if (it.id != id) {
                it.children.findNode(id)
            } else {
                it
            }
        }

    val instance: ModelInstance = ModelInstance(model.apply {
        //show only specific parts
        val nodes = this.nodes.toList()
        this.nodes.clear()
        parts.forEach {
            val node = nodes.findNode(it)
            this.nodes.add(node)
        }
        //copy animations
        this@CharacterController.animations.forEach { (id, anim) ->
            animations.add(copyAnimation(anim, id))
        }
    })

    fun copyAnimation(sourceAnim: Animation, newId: String): Animation {
        val newAnim = Animation()
        newAnim.id = newId
        newAnim.duration = sourceAnim.duration

        // For each node animation in the source
        for (nodeAnim in sourceAnim.nodeAnimations) {
            val sourceNodeId = nodeAnim.node.id

            // Find matching node in target model
            val targetNode = model.nodes.findNode(sourceNodeId)

            if (targetNode != null) {
                // Create new NodeAnimation pointing to target node
                val newNodeAnim = NodeAnimation()
                newNodeAnim.node = targetNode

                // Copy keyframes using GdxArray
                if (nodeAnim.translation != null) {
                    newNodeAnim.translation = GdxArray()
                    for (kf in nodeAnim.translation) {
                        newNodeAnim.translation.add(NodeKeyframe(kf.keytime, Vector3(kf.value)))
                    }
                }

                if (nodeAnim.rotation != null) {
                    newNodeAnim.rotation = GdxArray()
                    for (kf in nodeAnim.rotation) {
                        newNodeAnim.rotation.add(NodeKeyframe(kf.keytime, Quaternion(kf.value)))
                    }
                }

                if (nodeAnim.scaling != null) {
                    newNodeAnim.scaling = GdxArray()
                    for (kf in nodeAnim.scaling) {
                        newNodeAnim.scaling.add(NodeKeyframe(kf.keytime, Vector3(kf.value)))
                    }
                }

                newAnim.nodeAnimations.add(newNodeAnim)
            } else {
                println("Warning: Node $sourceNodeId not found in target model")
            }
        }

        return newAnim
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
    var colorBodyArt = hex("ff0000")

    //    var colorBodyArt = hex("299F2A")
    var colorEyes = hex("000000")
    val bodyArtAmount = 1f
}

data class MaskTextures(
    val baseTexture: Texture,
    val mask01: Texture,
    val mask02: Texture,
    val mask03: Texture,
    val mask04: Texture,
    val mask05: Texture
)

