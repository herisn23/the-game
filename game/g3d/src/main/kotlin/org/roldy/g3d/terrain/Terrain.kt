package org.roldy.g3d.terrain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import org.roldy.core.EnvironmentalRenderable
import org.roldy.core.Vector2Int
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.map.MapSize
import org.roldy.core.map.NoiseData
import org.roldy.g3d.Biome

class Terrain(
    val noiseData: Map<Vector2Int, NoiseData>,
    mapSize: MapSize,
    val scale: Float = 1f,
    val heightScale: Float = 150f,
    val chunkSize: Int = 255,
) : AutoDisposableAdapter(), EnvironmentalRenderable {

    private val width = mapSize.width
    private val depth = mapSize.height
    private val chunks = mutableListOf<TerrainChunk>()

    private val textureManager by disposable { BiomeTextureManager() }
    private val shader: ShaderProgram by disposable { ShaderLoader.terrainShader }
    private val lightDir = Vector3(-1f, -0.8f, -0.2f).nor()

    // Global biome to slot mapping (max 4 for GL20)
    private val globalBiomeSlots = mutableMapOf<Biome, Int>()
    private val slotBiomes = arrayOfNulls<Biome>(4)

    init {
        // First pass: find all biomes used in entire terrain
        analyzeGlobalBiomes()

        // Second pass: create chunks with global mapping
        createChunks()
    }

    private fun analyzeGlobalBiomes() {
        val biomeCounts = mutableMapOf<Biome, Int>()

        for (x in 0 until width) {
            for (z in 0 until depth) {
                val data = noiseData[Vector2Int(x, z)] ?: continue

                // Calculate slope
                val left = noiseData[Vector2Int(maxOf(0, x - 1), z)]?.elevation ?: data.elevation
                val right = noiseData[Vector2Int(minOf(width - 1, x + 1), z)]?.elevation ?: data.elevation
                val up = noiseData[Vector2Int(x, maxOf(0, z - 1))]?.elevation ?: data.elevation
                val down = noiseData[Vector2Int(x, minOf(depth - 1, z + 1))]?.elevation ?: data.elevation

                val normal = Vector3(left - right, 2f * scale / heightScale, up - down).nor()
                val slope = 1f - normal.y

                val biome = Biome.fromClimate(data.elevation, data.temperature, data.moisture, slope)
                biomeCounts[biome] = (biomeCounts[biome] ?: 0) + 1
            }
        }

        // Take top 4 most common biomes
        val topBiomes = biomeCounts.entries
            .sortedByDescending { it.value }
            .take(4)
            .map { it.key }

        topBiomes.forEachIndexed { slot, biome ->
            globalBiomeSlots[biome] = slot
            slotBiomes[slot] = biome
        }

        // Debug
        println("=== SLOT MAPPING ===")
        println("globalBiomeSlots: $globalBiomeSlots")
        println("slotBiomes: ${slotBiomes.toList()}")
    }

    private fun createChunks() {
        val chunksX = (width - 1) / chunkSize + 1
        val chunksZ = (depth - 1) / chunkSize + 1

        for (cz in 0 until chunksZ) {
            for (cx in 0 until chunksX) {
                val startX = cx * chunkSize
                val startZ = cz * chunkSize
                val endX = minOf(startX + chunkSize + 1, width)
                val endZ = minOf(startZ + chunkSize + 1, depth)

                chunks.add(
                    TerrainChunk(
                        startX, startZ, endX, endZ,
                        noiseData, width, depth, scale, heightScale,
                        globalBiomeSlots  // Pass global mapping
                    )
                )
            }
        }
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
        shader.bind()
        shader.setUniformMatrix("u_projViewTrans", camera.combined)
        shader.setUniformf("u_lightDir", lightDir)
        shader.setUniformf("u_ambientLight", 0.3f)

//        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
//        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)
//        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
//        Gdx.gl.glCullFace(GL20.GL_BACK)
        // Clear all texture units first
        repeat(4) {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + it)
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0)

            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + it)
            val biome = slotBiomes[it] ?: Biome.GRASSLAND
            textureManager[biome].bind()
            shader.setUniformi("u_texture$it", it)
        }

        chunks.forEach { chunk ->
            if (camera.frustum.boundsInFrustum(chunk.boundingBox)) {
                renderChunk(chunk)
            }
        }
    }

    private fun renderChunk(chunk: TerrainChunk) {
        // Bind textures for this chunk's biomes
        val biomeList = chunk.usedBiomes.take(4).toList()

        biomeList.forEachIndexed { slot, biome ->
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + slot)
            textureManager[biome].bind()
            shader.setUniformi("u_texture$slot", slot)
        }

        // Fill remaining slots with first texture
        biomeList.forEachIndexed { slot, biome ->
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + slot)
            textureManager[biome].bind()
            shader.setUniformi("u_texture$slot", slot)
        }
        for (slot in biomeList.size until 4) {

        }

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)

        shader.setUniformMatrix("u_worldTrans", chunk.instance.transform)
        chunk.instance.model.meshes.first().render(shader, GL20.GL_TRIANGLES)
    }

    // Debug: count visible chunks
    fun getVisibleCount(camera: Camera): Int {
        return chunks.count { camera.frustum.boundsInFrustum(it.boundingBox) }
    }
    fun getTotalCount(): Int {
        return chunks.size
    }

}