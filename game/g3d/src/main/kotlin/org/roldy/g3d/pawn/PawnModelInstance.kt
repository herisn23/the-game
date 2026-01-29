package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.model.NodePart
import com.badlogic.gdx.math.Vector3
import org.roldy.core.camera.FloatingOriginModelInstance

class PawnModelInstance(model: Model) : FloatingOriginModelInstance(model) {
    private val nodeParts: List<Pair<Node, List<NodePart>>>
    val allNodes: List<Node>

    val meshMap: Map<Pair<String, Vector3>, Node> by lazy {
        nodeParts.flatMap { (node, parts) ->
            parts.map {
                (it.meshPart.mappingId()) to node
            }
        }.toMap()
    }

    init {
        allNodes = nodes.collect()
        nodeParts = allNodes.map {
            it to it.parts.toList()
        }
    }

    fun setVisibility(visibleParts: List<String>) {
        nodeParts.forEach { (node, parts) ->
            parts.forEach {
                it.enabled = visibleParts.contains(node.id)
            }
        }
    }

    private fun Iterable<Node>.collect(): List<Node> =
        flatMap {
            it.children.collect() + listOf(it)
        }


    fun MeshPart.mappingId() =
        id to center
}