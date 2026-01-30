package org.roldy.g3d.terrain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import org.roldy.core.Renderable
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.map.MapSize
import org.roldy.core.map.MapTerrainData
import org.roldy.core.utils.invoke

class Terrain(
    val mapTerrainData: MapTerrainData,
    val light: DirectionalLight,
    val ambientLight: ColorAttribute,
    val camera: Camera,
    mapSize: MapSize,
    val scale: Float = 100f,
    val heightScale: Float = 150f * scale,
    val chunkSize: Int = 255,
) : AutoDisposableAdapter(), Renderable {
    private val frustum = FrustumCuller()
    private val noiseData = mapTerrainData.noiseData
    private val splatMaps = mapTerrainData.splatMaps
    private val textureScale = 1f
    val width = mapSize.width
    val depth = mapSize.height
    val chunks = mutableListOf<TerrainChunk>()
    var originOffset: Vector3 = Vector3()

    private val atlasAlbedo by disposable { AtlasLoader.terrainAlbedo }
    private val atlasNormal by disposable { AtlasLoader.terrainNormal }

    // Load textures directly with mipmaps
    private val texturesAlbedo: Texture by lazy { atlasAlbedo.textures.first() }
    private val texturesNormal: Texture by lazy { atlasNormal.textures.first() }

    private val shader: ShaderProgram by disposable { ShaderLoader.terrainShader }

    // Atlas configuration
    private val tileSize by lazy { atlasAlbedo.regions.first().regionHeight }
    private val atlasWidth by lazy { texturesAlbedo.width }
    private val atlasHeight by lazy { texturesAlbedo.height }
    private val materialCount by lazy { AtlasLoader.terrainAlbedo.regions.size }
    private val padding = 0

    // Pre-computed UVs
    val materialUVs by lazy {
        generateTerrainMaterialUVs(tileSize, atlasWidth, atlasHeight, materialCount, padding)
    }

    private inner class FrustumCuller {
        private val tmpBox = BoundingBox()

        fun isVisible(chunk: TerrainChunk, camera: Camera): Boolean {
            val offset = originOffset

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
                        noiseData, width, depth, scale, heightScale
                    ).disposable()
                )
            }
        }
    }

    context(delta: Float)
    override fun render() {
        shader.bind()

        // Camera
        shader("u_projViewTrans") {
            setUniformMatrix(it, camera.combined)
        }
        shader("u_cameraPosition") {
            setUniformf(it, camera.position)
        }

        // Offset to moving chunks
        shader("u_renderOffset") {
            setUniformf(it, originOffset.x, originOffset.y, originOffset.z)
        }

        // Lighting
        shader("u_lightDirection") {
            setUniformf(it, light.direction)
        }
        shader("u_lightColor") {
            setUniformf(it, light.color.r, light.color.g, light.color.b)
        }
        shader("u_ambientLight") {
            setUniformf(it, ambientLight.color.r, ambientLight.color.g, ambientLight.color.b)
        }

        // Material UVs
        for (i in 0 until materialCount) {
            val uv = materialUVs[i]
            shader("u_uv$i") {
                setUniformf(it, uv.offset.x, uv.offset.y, uv.scale.x, uv.scale.y)
            }
        }

        // Texture samplers

        shader("u_textureScale") {
            setUniformf(it, textureScale)
        }
        shader("u_albedoAtlas") {
            setUniformi(it, 0)
        }
        shader("u_normalAtlas") {
            setUniformi(it, 1)
        }
        for (i in 0 until minOf(7, splatMaps.size)) {
            shader("u_splat$i") {
                setUniformi(it, 2 + i)
            }
        }

        // Bind textures
        texturesAlbedo.bind(0)
        texturesNormal.bind(1)
        for (i in 0 until minOf(7, splatMaps.size)) {
            splatMaps[i].bind(2 + i)
        }

        // Reset active texture
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)

        // Render visible chunks
        frustum.getVisibleChunks(chunks, camera).forEach { chunk ->
            shader("u_worldTrans") {
                setUniformMatrix(it, chunk.instance.transform)
            }
            chunk.instance.model.meshes.first().render(shader, GL20.GL_TRIANGLES)
        }
    }

    fun getVisibleCount(camera: Camera): Int =
        frustum.getVisibleChunks(chunks, camera).size

    fun getTotalCount(): Int = chunks.size
}

