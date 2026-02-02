package org.roldy.g3d.terrain

import com.badlogic.gdx.math.Vector3
import org.roldy.core.HeightSampler
import org.roldy.core.IVector2Int
import org.roldy.core.Vector2Int
import org.roldy.core.map.NoiseData

class TerrainHeightSampler(
    private val width: Int,
    private val depth: Int,
    private val scale: Float,
    noiseData: Map<IVector2Int, NoiseData>,
    heightScale: Float,
) : HeightSampler {
    var originOffset = Vector3()
    private val heights = Array(depth) { FloatArray(width) { 0f } }

    init {
        for (z in 0 until depth) {
            for (x in 0 until width) {
                val data = noiseData[Vector2Int(x, z)]!!
                setHeight(x, z, data.elevation * heightScale)
            }
        }
    }

    fun setHeight(gridX: Int, gridZ: Int, height: Float) {
        if (gridX in 0 until width && gridZ in 0 until depth) {
            heights[gridZ][gridX] = height
        }
    }

    override fun getHeightAt(localX: Float, localZ: Float): Float {
        val terrainX = (localX + originOffset.x) / scale + width / 2f
        val terrainZ = (localZ + originOffset.z) / scale + depth / 2f

        val x0 = terrainX.toInt().coerceIn(0, width - 2)
        val z0 = terrainZ.toInt().coerceIn(0, depth - 2)
        val x1 = x0 + 1
        val z1 = z0 + 1

        val fx = (terrainX - x0).coerceIn(0f, 1f)
        val fz = (terrainZ - z0).coerceIn(0f, 1f)

        val h00 = heights[z0][x0]
        val h10 = heights[z0][x1]
        val h01 = heights[z1][x0]
        val h11 = heights[z1][x1]

        val h0 = h00 + (h10 - h00) * fx
        val h1 = h01 + (h11 - h01) * fx

        return h0 + (h1 - h0) * fz
    }

    override fun isInBounds(localX: Float, localZ: Float): Boolean {
        val terrainX = localX / scale + width / 2f
        val terrainZ = localZ / scale + depth / 2f
        return terrainX in 0f..(width - 1).toFloat() &&
                terrainZ in 0f..(depth - 1).toFloat()
    }
}