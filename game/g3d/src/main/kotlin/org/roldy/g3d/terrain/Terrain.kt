package org.roldy.g3d.terrain

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.EnvironmentalRenderable
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.camera.OffsetProvider
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.map.MapSize
import org.roldy.core.map.MapTerrainData
import org.roldy.core.shader.util.shaderProvider
import org.roldy.core.utils.repeat


class Terrain(
    val mapTerrainData: MapTerrainData,
    internal val offsetProvider: OffsetProvider,
    mapSize: MapSize
) : AutoDisposableAdapter(), EnvironmentalRenderable {
    private val noiseData = mapTerrainData.noiseData
    private val chunkSize: Int = 255
    private val frustum = FrustumCuller()

    val texture: Texture by disposable { Texture(AtlasLoader.terrainTileSet) }
    val scale: Float = 1f
    val heightScale: Float = 150f * scale
    val width = mapSize.width
    val depth = mapSize.height
    var normalStrength: Float = 1f


    internal val textureScale = 10f
    internal val chunks = mutableListOf<TerrainChunk>()
    internal val modelBatch = ModelBatch(shaderProvider {
        TerrainShader(this@Terrain, it)
    })

    internal inner class FrustumCuller {
        private val tmpBox = BoundingBox()

        fun isVisible(chunk: TerrainChunk, camera: Camera): Boolean {
            val offset = offsetProvider.shiftOffset
            // Copy chunk bounds and apply offset
            tmpBox.set(chunk.boundingBox)
            tmpBox.min.sub(offset)
            tmpBox.max.sub(offset)
            return camera.frustum.boundsInFrustum(tmpBox)
        }

        fun getVisibleChunks(chunks: List<TerrainChunk>, camera: Camera): List<TerrainChunk> {
            return chunks.filter { isVisible(it, camera) }
        }
    }


    init {
        createChunks()
    }

    context(delta: Float, environment: Environment, camera: Camera)
    override fun render() {
        modelBatch.begin(camera)
        // Render visible chunks
        frustum.getVisibleChunks(chunks, camera).forEach { chunk ->
            modelBatch.render(chunk.instance, environment)
        }
        modelBatch.end()
    }

    fun getVisibleCount(camera: Camera): Int =
        frustum.getVisibleChunks(chunks, camera).size

    fun getTotalCount(): Int = chunks.size

    private fun createChunks() {
        val chunksX = (width - 1) / chunkSize + 1
        val chunksZ = (depth - 1) / chunkSize + 1
        repeat(0..<chunksX, 0..<chunksZ) { cx, cz ->
            val startX = cx * chunkSize
            val startZ = cz * chunkSize
            val endX = minOf(startX + chunkSize + 1, width)
            val endZ = minOf(startZ + chunkSize + 1, depth)

            chunks.add(
                TerrainChunk(
                    startX, startZ, endX, endZ,
                    noiseData, width, depth, scale, heightScale
                ).disposable()
            )
        }
    }
}

