package org.roldy.g3d.terrain

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.Vector2Int
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.map.NoiseData
import org.roldy.g3d.Biome

class TerrainChunk(
    val startX: Int,
    val startZ: Int,
    val endX: Int,
    val endZ: Int,
    private val noiseData: Map<Vector2Int, NoiseData>,
    private val width: Int,
    private val depth: Int,
    private val scale: Float,
    private val heightScale: Float,
    private val globalBiomeSlots: Map<Biome, Int>  // Use global mapping
) : AutoDisposableAdapter() {
    val model: Model
    val instance: ModelInstance
    val boundingBox = BoundingBox()

    // Biomes used in this chunk (max 4 for shader)
    val usedBiomes = mutableSetOf<Biome>()

    init {
        model = createChunkModel().disposable()
        instance = ModelInstance(model)
        instance.calculateBoundingBox(boundingBox)
    }

    private fun createChunkModel(): Model {
        val chunkW = endX - startX
        val chunkD = endZ - startZ
        val vertexCount = chunkW * chunkD
        val indexCount = (chunkW - 1) * (chunkD - 1) * 6

        val vertexSize = 11
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

                // TexCoord
                vertices[vIndex++] = x * 0.1f
                vertices[vIndex++] = z * 0.1f

                // Calculate biome using global slots
                val slope = 1f - normal.y
                val primaryBiome = Biome.fromClimate(data.elevation, data.temperature, data.moisture, slope)
                val secondaryBiome = getSecondaryBiome(data, slope, primaryBiome)
                val blendWeight = calculateBlendWeight(data, slope, primaryBiome, secondaryBiome)

                // Use GLOBAL slot mapping - fallback to 0 if biome not in top 4
                val primarySlot = globalBiomeSlots[primaryBiome] ?: findClosestSlot(primaryBiome)
                val secondarySlot = globalBiomeSlots[secondaryBiome] ?: primarySlot

                vertices[vIndex++] = primarySlot.toFloat()
                vertices[vIndex++] = secondarySlot.toFloat()
                vertices[vIndex++] = blendWeight
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
            VertexAttribute.TexCoords(0),
            VertexAttribute(VertexAttributes.Usage.Generic, 3, "a_biomeData")
        ).apply {
            setVertices(vertices)
            setIndices(indices)
        }

        val builder = ModelBuilder()
        builder.begin()
        builder.part("chunk", mesh, GL20.GL_TRIANGLES, Material())
        return builder.end()
    }

    // Find closest available biome if this one isn't in top 4
    private fun findClosestSlot(biome: Biome): Int {
        // Map any biome to one of the top 4: BEACH=0, WATER=1, GRASSLAND=2, FOREST=3
        val fallbackSlot = when (biome) {
            Biome.BEACH -> 0
            Biome.WATER -> 1
            Biome.GRASSLAND -> 2
            Biome.FOREST -> 3

            // Map similar biomes to available slots
            Biome.DESERT, Biome.SAVANNA -> 0           // sandy -> BEACH
            Biome.SWAMP -> 1                            // wet -> WATER
            Biome.RAINFOREST -> 3                       // trees -> FOREST
            Biome.TUNDRA, Biome.SNOW -> 2              // cold/flat -> GRASSLAND
            Biome.MOUNTAIN, Biome.DIRT -> 2            // rocky -> GRASSLAND

            else -> 2  // Default to GRASSLAND
        }
        return fallbackSlot
    }

    private fun getSecondaryBiome(data: NoiseData, slope: Float, primary: Biome): Biome {
        val epsilon = 0.05f
        val candidates = listOf(
            Biome.fromClimate(data.elevation + epsilon, data.temperature, data.moisture, slope),
            Biome.fromClimate(data.elevation - epsilon, data.temperature, data.moisture, slope),
            Biome.fromClimate(data.elevation, data.temperature + epsilon, data.moisture, slope),
            Biome.fromClimate(data.elevation, data.temperature, data.moisture + epsilon, slope)
        ).filter { it != primary }

        return candidates.firstOrNull() ?: primary
    }

    private fun calculateBlendWeight(data: NoiseData, slope: Float, primary: Biome, secondary: Biome): Float {
        if (primary == secondary) return 0f

        val e = (data.elevation * 20f) % 1f
        val t = (data.temperature * 20f) % 1f
        val m = (data.moisture * 20f) % 1f

        val edgeDist = minOf(minOf(e, 1f - e), minOf(t, 1f - t), minOf(m, 1f - m))
        return (0.5f - edgeDist).coerceIn(0f, 0.4f)
    }
}