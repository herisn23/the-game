package org.roldy.g3d.pawn.utils

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.model.Animation
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.roldy.core.logger
import com.badlogic.gdx.utils.Array as GdxArray

private val cLogger by logger("CopyAnimation")

fun Model.copyAnimation(sourceAnim: Animation, newId: String): Animation {

    val newAnim = Animation()
    newAnim.id = newId
    newAnim.duration = sourceAnim.duration

    // For each node animation in the source
    for (nodeAnim in sourceAnim.nodeAnimations) {
        val sourceNodeId = nodeAnim.node.id

        // Find matching node in target model
        val targetNode = nodes.findNode(sourceNodeId)

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
            cLogger.warn("Node $sourceNodeId not found in target model")
        }
    }

    return newAnim
}