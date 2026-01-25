package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.model.NodePart
import com.badlogic.gdx.math.Vector3
import org.roldy.g3d.pawn.utils.copyAnimation

class PawnModelInstance(modelData: Data, bodyType: BodyType) : ModelInstance(modelData.run {
    base.apply {
        additions.forEach {
            nodes.addAll(it.nodes)
        }

        PawnAnimations[bodyType].all.forEach { anim ->
            animations.add(copyAnimation(anim.model.get().animations.first(), anim.id))
        }
    }
}) {
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
        nodes.forEach {
            if (!it.id.contains("Hips")) {
                println(it.id)
            }

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

    data class Data(
        val base: Model,
        val additions: List<Model>
    )

    fun MeshPart.mappingId() =
        id to center
}