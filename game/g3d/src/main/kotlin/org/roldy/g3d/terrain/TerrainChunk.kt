package org.roldy.g3d.terrain

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.Vector2Int
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.map.NoiseData

internal class TerrainChunk(
    startX: Int, startZ: Int, endX: Int, endZ: Int,
    private val width: Int, private val depth: Int,
    private val noiseData: Map<Vector2Int, NoiseData>,
    private val scale: Float,
    private val heightScale: Float,
) : AutoDisposableAdapter() {
    val model: Model
    val instance: ModelInstance
    val boundingBox = BoundingBox()

    init {
        model = createChunkModel(startX, startZ, endX, endZ).disposable()
        instance = ModelInstance(model)
        instance.calculateBoundingBox(boundingBox)
    }

    private fun createChunkModel(startX: Int, startZ: Int, endX: Int, endZ: Int): Model {
        val chunkW = endX - startX
        val chunkD = endZ - startZ
        val vertexCount = chunkW * chunkD
        val indexCount = (chunkW - 1) * (chunkD - 1) * 6
        val vertexSize = 6

        val vertices = FloatArray(vertexCount * vertexSize)
        val indices = ShortArray(indexCount)

        var vIndex = 0
        for (z in startZ until endZ) {
            for (x in startX until endX) {
                val data = noiseData[Vector2Int(x.coerceIn(0, width - 1), z.coerceIn(0, depth - 1))]!!
                val height = data.elevation * heightScale

                vertices[vIndex++] = (x - width / 2f) * scale
                vertices[vIndex++] = height
                vertices[vIndex++] = (z - depth / 2f) * scale

                val left = (noiseData[Vector2Int(maxOf(0, x - 1), z.coerceIn(0, depth - 1))]?.elevation
                    ?: data.elevation) * heightScale
                val right = (noiseData[Vector2Int(minOf(width - 1, x + 1), z.coerceIn(0, depth - 1))]?.elevation
                    ?: data.elevation) * heightScale
                val up = (noiseData[Vector2Int(x.coerceIn(0, width - 1), maxOf(0, z - 1))]?.elevation
                    ?: data.elevation) * heightScale
                val down = (noiseData[Vector2Int(x.coerceIn(0, width - 1), minOf(depth - 1, z + 1))]?.elevation
                    ?: data.elevation) * heightScale

                val normal = Vector3(left - right, 2f * scale, up - down).nor()
                vertices[vIndex++] = normal.x
                vertices[vIndex++] = normal.y
                vertices[vIndex++] = normal.z
            }
        }

        var iIndex = 0
        for (z in 0 until chunkD - 1) {
            for (x in 0 until chunkW - 1) {
                val topLeft = z * chunkW + x
                val topRight = topLeft + 1
                val bottomLeft = (z + 1) * chunkW + x
                val bottomRight = bottomLeft + 1

                indices[iIndex++] = topLeft.toShort()
                indices[iIndex++] = bottomLeft.toShort()
                indices[iIndex++] = topRight.toShort()

                indices[iIndex++] = topRight.toShort()
                indices[iIndex++] = bottomLeft.toShort()
                indices[iIndex++] = bottomRight.toShort()
            }
        }

        val mesh = Mesh(
            true, vertexCount, indexCount,
            VertexAttribute.Position(),
            VertexAttribute.Normal()
        ).apply {
            setVertices(vertices)
            setIndices(indices)
        }

        val builder = ModelBuilder()
        builder.begin()
        builder.part(
            "chunk",
            mesh,
            GL20.GL_TRIANGLES,
            Material().apply { set(ColorAttribute.createDiffuse(Color.GREEN)) }
        )
        return builder.end()
    }
}