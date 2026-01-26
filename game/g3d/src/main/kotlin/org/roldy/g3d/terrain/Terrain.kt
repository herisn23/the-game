package org.roldy.g3d.terrain

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.roldy.core.EnvironmentalRenderable
import org.roldy.core.Vector2Int
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.map.MapSize
import org.roldy.core.map.NoiseData

class ChunkedTerrain(
    private val noiseData: Map<Vector2Int, NoiseData>,
    mapSize: MapSize,
    private val scale: Float = 1f,
    private val heightScale: Float = 150f,
    chunkSize: Int = 128,
) : AutoDisposableAdapter(), EnvironmentalRenderable {
    val batch by disposable { ModelBatch() }

    private val width = mapSize.width
    private val depth = mapSize.height

    private val chunks = mutableListOf<TerrainChunk>()

    init {
        val chunksX = (width + chunkSize - 1) / chunkSize
        val chunksZ = (depth + chunkSize - 1) / chunkSize
        println("Creating terrain: ${width}x${depth}, chunk size: $chunkSize")
        println("Chunks: ${chunksX}x${chunksZ} = ${chunksX * chunksZ} total")

        for (cz in 0 until chunksZ) {
            for (cx in 0 until chunksX) {
                val startX = cx * chunkSize
                val startZ = cz * chunkSize
                val endX = minOf(startX + chunkSize + 1, width)
                val endZ = minOf(startZ + chunkSize + 1, depth)
                chunks.add(
                    TerrainChunk(
                        startX,
                        startZ,
                        endX,
                        endZ,
                        width,
                        depth,
                        noiseData,
                        scale,
                        heightScale,
                    ).disposable()
                )
            }
        }

        println("Chunks created: ${chunks.size}")
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        chunks.forEach { chunk ->
            chunk.instance.transform.setTranslation(x, y, z)
            // Update bounding box with new position
            chunk.instance.calculateBoundingBox(chunk.boundingBox)
        }
    }

    context(delta: Float, environment: Environment, camera: Camera)
    override fun render() {
        val frustum = camera.frustum
        batch.begin(camera)
        chunks.forEach { chunk ->
            // Only render if visible
            if (frustum.boundsInFrustum(chunk.boundingBox)) {
                batch.render(chunk.instance, environment)
            }
        }
        batch.end()
    }

    // Debug: count visible chunks
    fun getVisibleCount(camera: Camera): Int {
        return chunks.count { camera.frustum.boundsInFrustum(it.boundingBox) }
    }

    override fun dispose() {
        chunks.forEach { it.dispose() }
    }

}