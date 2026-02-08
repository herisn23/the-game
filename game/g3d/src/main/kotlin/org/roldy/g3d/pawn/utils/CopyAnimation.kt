package org.roldy.g3d.pawn.utils

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.model.Animation
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.roldy.core.asset.modelScale
import org.roldy.core.logger
import com.badlogic.gdx.utils.Array as GdxArray

private val cLogger by logger("CopyAnimation")

fun Model.copyAnimation(sourceAnim: Animation, newId: String): Animation {
    val newAnim = Animation()
    newAnim.id = newId
    newAnim.duration = sourceAnim.duration

    for (nodeAnim in sourceAnim.nodeAnimations) {
        val sourceNodeId = nodeAnim.node.id
        val targetNode = nodes.findNode(sourceNodeId)

        if (targetNode != null) {
            val newNodeAnim = NodeAnimation()
            newNodeAnim.node = targetNode

            // Copy and SCALE translation keyframes
            if (nodeAnim.translation != null) {
                newNodeAnim.translation = GdxArray()
                for (kf in nodeAnim.translation) {
                    newNodeAnim.translation.add(
                        NodeKeyframe(kf.keytime, Vector3(kf.value).scl(modelScale))
                    )
                }
            }

            // Copy rotation as-is
            if (nodeAnim.rotation != null) {
                newNodeAnim.rotation = GdxArray()
                for (kf in nodeAnim.rotation) {
                    newNodeAnim.rotation.add(
                        NodeKeyframe(kf.keytime, Quaternion(kf.value))
                    )
                }
            }

            if (nodeAnim.scaling != null) {
                newNodeAnim.scaling = GdxArray()
                for (kf in nodeAnim.scaling) {
                    newNodeAnim.scaling.add(NodeKeyframe(kf.keytime, Vector3(kf.value)))
                }
            }

            newAnim.nodeAnimations.add(newNodeAnim)
        }
    }

    return newAnim
}