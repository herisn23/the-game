package org.roldy.g3d.terrain

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.glutils.ShaderProgram
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
    val scale: Float = 10f,
    val heightScale: Float = 1500f,
    val chunkSize: Int = 255,
) : AutoDisposableAdapter(), Renderable {
    var debugMode = 0

    private val noiseData = mapTerrainData.noiseData
    private val splatMaps = mapTerrainData.splatMaps
    val width = mapSize.width
    val depth = mapSize.height
    private val chunks = mutableListOf<TerrainChunk>()

    // Load textures directly with mipmaps
    private val texturesAlbedo: Texture by disposable {
        AtlasLoader.terrainAlbedo.textures.first()
    }

    private val texturesNormal: Texture by disposable {
        AtlasLoader.terrainNormal.textures.first()
    }

    private val shader: ShaderProgram by disposable { ShaderLoader.terrainShader }

    // Atlas configuration
    private val tileSize = 512
    private val atlasWidth = 4096
    private val atlasHeight = 2048
    private val materialCount = 28
    private val padding = 16

    // Pre-computed UVs
    val materialUVs = generateTerrainMaterialUVs(tileSize, atlasWidth, atlasHeight, materialCount, padding)

    init {
        createChunks()
        // Log what UV values are being generated
        for (i in 0 until 8) {
            val uv = materialUVs[i]
            Gdx.app.log(
                "UV",
                "materialUVs[$i] = offset(${uv.offset.x}, ${uv.offset.y}), scale(${uv.scale.x}, ${uv.scale.y})"
            )
        }
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

    fun setPosition(x: Float, y: Float, z: Float) {
        chunks.forEach { chunk ->
            chunk.instance.transform.setTranslation(x, y, z)
            chunk.instance.calculateBoundingBox(chunk.boundingBox)
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

        // Debug mode
        shader("u_debugMode") {
            setUniformi(it, debugMode)
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
        chunks.forEach { chunk ->
            if (camera.frustum.boundsInFrustum(chunk.boundingBox)) {
                shader("u_worldTrans") {
                    setUniformMatrix(it, chunk.instance.transform)
                }
                chunk.instance.model.meshes.first().render(shader, GL20.GL_TRIANGLES)
            }
        }
    }

    fun getVisibleCount(camera: Camera): Int =
        chunks.count { camera.frustum.boundsInFrustum(it.boundingBox) }

    fun getTotalCount(): Int = chunks.size
}