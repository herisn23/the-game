package org.roldy.g3d.environment

import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.model.Node
import org.roldy.core.configuration.data.MeshData
import org.roldy.core.shader.util.ShaderUserData

class EnvModelConfiguration(
    val modelName: String,
    private val materialsMap: List<Pair<String, Material>>,
    private val model: Model,
    private val collisionModel: Model?,
    meshes: List<MeshData>
) {
    val meshes = meshes.groupBy { it.lod }

    val udata = ShaderUserData()
    private fun createInstances() =
        meshes.map { (lod, meshes) ->
            ModelInstance(model).apply {
                userData = udata
                this.materials.clear()
                val node = nodes.first()
                node.assignMaterial(node, meshes)
                node.children.removeAll { meshes.none { m -> m.meshName == it.id } }
                when (node.children.count()) {
                    0 -> node.assignMaterial(node, meshes)
                    else -> {
                        node.children.forEach {
                            it.assignMaterial(node, meshes)
                        }
                    }
                }
            }.let { instance ->
                fun List<Node>.collect(): List<Node> =
                    this + flatMap { it.children.toList().collect() }
                //temporary code to skip unknown shaders
                val hashKnownShder = instance.nodes.toList().collect().any { node ->
                    node.parts.any { p ->
                        val shaderName = materialsMap.find { it.second.id == p.material.id }?.first
                        shaderName == SyntyShaderNames.FOLIAGE || shaderName == SyntyShaderNames.GENERIC
                    }

                }
                if (hashKnownShder) {
                    lod to instance
                } else {
                    null
                }
            }
        }.filterNotNull()

    fun List<MeshData>.find(name: String) =
        find { it.meshName == name }

    fun Node.assignMaterial(parent: Node, data: List<MeshData>) {
        val data = data.find(id)
        if (data == null) {
            parent.removeChild(this)
        } else {
            parts.forEach { part ->
                val material = materialsMap.first { it.second.id == data.materialName }
                part.material = material.second
                if (material.first == SyntyShaderNames.FOLIAGE) {
                    udata.foliage = true
                }
            }
        }
    }

    fun createInstance(): EnvModelInstance? {
        val instances = createInstances()
        return if (instances.isNotEmpty()) {
            EnvModelInstance(
                modelName,
                collisionModel,
                udata.foliage,
                instances.toMap()
            ).apply {
                udata.instance = this
            }
        } else {
            null
        }
    }

}