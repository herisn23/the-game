package org.roldy.g3d.terrain

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.Vector2Int
import org.roldy.core.camera.FloatingOriginEntity
import org.roldy.core.camera.FloatingOriginModelInstance
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.map.NoiseData

class TerrainChunk(
    val startX: Int,
    val startZ: Int,
    val endX: Int,
    val endZ: Int,
    private val noiseData: Map<Vector2Int, NoiseData>,
    private val width: Int,
    private val depth: Int,
    private val scale: Float,
    private val heightScale: Float
) : AutoDisposableAdapter(), FloatingOriginEntity {
    val model: Model
    val instance: FloatingOriginModelInstance

    // Local space bounding box (never changes)
    private val localBoundingBox = BoundingBox()

    // World space bounding box (updated when position changes)
    val boundingBox = BoundingBox()

    init {
        model = createChunkModel().disposable()
        instance = object : FloatingOriginModelInstance(model) {}

        // Calculate local bounds once
        instance.calculateBoundingBox(localBoundingBox)
    }

    private fun createChunkModel(): Model {
        val chunkW = endX - startX
        val chunkD = endZ - startZ
        val vertexCount = chunkW * chunkD
        val indexCount = (chunkW - 1) * (chunkD - 1) * 6

        // Position(3) + Normal(3) + TexCoord0/tiled(2) + TexCoord1/splatmap(2) = 10
        val vertexSize = 10
        val vertices = FloatArray(vertexCount * vertexSize)
        val indices = ShortArray(indexCount)

        var vIndex = 0
        for (z in startZ until endZ) {
            for (x in startX until endX) {
                val clampedX = x.coerceIn(0, width - 1)
                val clampedZ = z.coerceIn(0, depth - 1)

                val data = noiseData[Vector2Int(clampedX, clampedZ)]!!
                val height = data.elevation * heightScale

                // Position
                vertices[vIndex++] = (x - width / 2f) * scale
                vertices[vIndex++] = height
                vertices[vIndex++] = (z - depth / 2f) * scale

                // Normal
                val left =
                    (noiseData[Vector2Int(maxOf(0, clampedX - 1), clampedZ)]?.elevation ?: data.elevation) * heightScale
                val right = (noiseData[Vector2Int(minOf(width - 1, clampedX + 1), clampedZ)]?.elevation
                    ?: data.elevation) * heightScale
                val up =
                    (noiseData[Vector2Int(clampedX, maxOf(0, clampedZ - 1))]?.elevation ?: data.elevation) * heightScale
                val down = (noiseData[Vector2Int(clampedX, minOf(depth - 1, clampedZ + 1))]?.elevation
                    ?: data.elevation) * heightScale

                val normal = Vector3(left - right, 2f * scale, up - down).nor()
                vertices[vIndex++] = normal.x
                vertices[vIndex++] = normal.y
                vertices[vIndex++] = normal.z

                // TexCoord0 - tiled UV for texture detail
                vertices[vIndex++] = x * 0.1f
                vertices[vIndex++] = z * 0.1f

                // TexCoord1 - terrain UV for splatmap sampling (0-1 across entire terrain)
                vertices[vIndex++] = x.toFloat() / (width - 1)
                vertices[vIndex++] = z.toFloat() / (depth - 1)
            }
        }

        // Indices
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
            VertexAttribute.Normal(),
            VertexAttribute.TexCoords(0),  // Tiled UV
            VertexAttribute.TexCoords(1)   // Splatmap UV
        ).apply {
            setVertices(vertices)
            setIndices(indices)
        }

        val builder = ModelBuilder()
        builder.begin()
        builder.part("chunk", mesh, GL20.GL_TRIANGLES, Material())
        return builder.end()
    }

    override var position: Vector3
        get() = instance.position
        set(value) {
            instance.position = value
            instance.calculateBoundingBox(boundingBox)
            boundingBox.mul(instance.transform)
        }

}