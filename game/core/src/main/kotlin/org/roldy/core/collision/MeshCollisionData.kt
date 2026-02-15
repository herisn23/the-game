package org.roldy.core.collision

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3

object MeshCollisionData {
    data class Triangle(val v0: Vector3, val v1: Vector3, val v2: Vector3)

    fun extractTriangles(modelInstance: ModelInstance): List<Triangle> {
        val triangles = mutableListOf<Triangle>()
        val tmpMatrix = Matrix4()

        for (node in modelInstance.nodes) {
            extractNodeTriangles(node, modelInstance.transform, tmpMatrix, triangles)
        }
        return triangles
    }

    private fun extractNodeTriangles(
        node: Node,
        instanceTransform: Matrix4,
        tmpMatrix: Matrix4,
        triangles: MutableList<Triangle>
    ) {
        // Combined transform: instance world transform * node global transform
        tmpMatrix.set(instanceTransform).mul(node.globalTransform)

        for (part in node.parts) {
            val mesh = part.meshPart.mesh
            val vertexSize = mesh.vertexSize / 4
            val offset = part.meshPart.offset
            val size = part.meshPart.size

            var posOffset = 0
            for (attr in mesh.vertexAttributes) {
                if (attr.usage == VertexAttributes.Usage.Position) {
                    posOffset = attr.offset / 4
                    break
                }
            }

            val vertices = FloatArray(mesh.numVertices * vertexSize)
            mesh.getVertices(vertices)

            val indices = ShortArray(mesh.numIndices)
            mesh.getIndices(indices)

            // Only process indices for this mesh part
            for (i in offset until offset + size step 3) {
                val i0 = indices[i].toInt() and 0xFFFF
                val i1 = indices[i + 1].toInt() and 0xFFFF
                val i2 = indices[i + 2].toInt() and 0xFFFF

                val v0 = Vector3(
                    vertices[i0 * vertexSize + posOffset],
                    vertices[i0 * vertexSize + posOffset + 1],
                    vertices[i0 * vertexSize + posOffset + 2]
                ).mul(tmpMatrix)

                val v1 = Vector3(
                    vertices[i1 * vertexSize + posOffset],
                    vertices[i1 * vertexSize + posOffset + 1],
                    vertices[i1 * vertexSize + posOffset + 2]
                ).mul(tmpMatrix)

                val v2 = Vector3(
                    vertices[i2 * vertexSize + posOffset],
                    vertices[i2 * vertexSize + posOffset + 1],
                    vertices[i2 * vertexSize + posOffset + 2]
                ).mul(tmpMatrix)

                triangles.add(Triangle(v0, v1, v2))
            }
        }

        // Process child nodes recursively
        for (child in node.children) {
            extractNodeTriangles(child, instanceTransform, tmpMatrix, triangles)
        }
    }
}