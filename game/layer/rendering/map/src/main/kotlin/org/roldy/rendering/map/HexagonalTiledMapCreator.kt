package org.roldy.rendering.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.x
import org.roldy.data.biome.BiomeData
import org.roldy.data.biome.match
import org.roldy.data.map.MapData
import org.roldy.data.map.NoiseData
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter

data class TileData(
    val terrain: Terrain,
    val noiseData: NoiseData
)

class HexagonalTiledMapCreator(
    val data: MapData,
    val noiseData: Map<Vector2Int, NoiseData>,
    val biomesData: List<BiomeData>,
    val generateColorsLayer: Boolean = false
) : AutoDisposableAdapter() {


    // Cache for terrain at each position
    private val terrainCache = mutableMapOf<Vector2Int, TileData>()
    private val fallbackTerrain = createFallbackTerrain()

    private val dirtUnderTextureRegion by lazy { TextureRegion(Texture("terrain/HexUnderDirt.png").disposable()) }
    private val waterUnderTextureRegion by lazy { TextureRegion(Texture("terrain/HexUnderWater.png").disposable()) }


    /**
     * Generates a complete TiledMap with biomes and optional transitions
     */
    fun create(): Pair<TiledMap, Map<Vector2Int, TileData>> {
        val tiledMap = TiledMap()
        val biomes = readBiomes(biomesData, data.tileSize)
        // Generate base terrain layer
        val baseLayer = generateBaseLayer(biomes)
        val underLayer = generateUnderLayer()

        tiledMap.layers.add(underLayer)
        tiledMap.layers.add(baseLayer)

        if (generateColorsLayer)
        // Generate colors layer
            tiledMap.layers.add(generateBiomeLayer())
        return tiledMap to terrainCache
    }

    private fun generateUnderLayer(): TiledMapTileLayer {
        val layer = TiledMapTileLayer(data.size.width, data.size.height, data.tileSize, data.tileSize)
        fun generateFor(size: Int, shouldApply: (Vector2Int) -> Boolean = { true }, vector: (Int) -> Vector2Int) {
            repeat(size) { index ->
                val vector = vector(index)
                if (shouldApply(vector)) {
                    val cell = TiledMapTileLayer.Cell().apply {
                        val terrainData = terrainCache[vector]
                        tile = StaticTiledMapTile(resolveUnderTileTexture(terrainData))
                        tile.offsetY = data.tileSize / -2f
                    }
                    layer.setCell(vector.x, vector.y, cell)
                }
            }
        }

        // first row
        generateFor(data.size.width) {
            it x 0
        }

        // left column
        generateFor(data.size.height, { it.y % 2 == 0 }) {
            0 x it
        }

        // right column
        generateFor(data.size.height, { it.y % 2 != 0 }) {
            data.size.width - 1 x it
        }

        return layer
    }

    fun resolveUnderTileTexture(tileData: TileData?): TextureRegion {
        val isWater = tileData?.terrain?.biome?.data?.name == "Water"
        return when {
            isWater -> waterUnderTextureRegion
            else -> dirtUnderTextureRegion
        }
    }

    private fun generateBiomeLayer(): TiledMapTileLayer {
        val layer = TiledMapTileLayer(data.size.width, data.size.height, data.tileSize, data.tileSize)
        terrainCache.forEach { (coords, terrain) ->
            val cell = TiledMapTileLayer.Cell().apply {
                tile = StaticTiledMapTile(terrain.terrain.biome.color)
            }
            layer.setCell(coords.x, coords.y, cell)
        }

        return layer
    }

    /**
     * Generates the base terrain layer without transitions
     */
    private fun generateBaseLayer(biomes: List<Biome>): TiledMapTileLayer {
        val layer = TiledMapTileLayer(data.size.width, data.size.height, data.tileSize, data.tileSize)
        noiseData.forEach { (coords, noiseData) ->
            val terrain = findTerrainForNoise(biomes, noiseData)

            // Cache the terrain for transition calculations
            terrainCache[coords] = TileData(
                terrain,
                noiseData
            )

            // Create cell with the selected terrain
            val cell = TiledMapTileLayer.Cell().apply {
                tile = StaticTiledMapTile(terrain.region)
            }
            layer.setCell(coords.x, coords.y, cell)
        }
        return layer
    }


    /**
     * Finds the appropriate terrain for the given noise data
     */
    private fun findTerrainForNoise(biomes: List<Biome>, noiseData: NoiseData): Terrain {
        // Find matching biome
        val biome = biomes.find { biome ->
            biome.data.elevation.match(noiseData.elevation) &&
                    biome.data.temperature.match(noiseData.temperature) &&
                    biome.data.moisture.match(noiseData.moisture)
        }

        // Find matching terrain within biome
        val terrain = biome?.terrains?.find { terrain ->
            terrain.data.elevation.match(noiseData.elevation) &&
                    terrain.data.temperature.match(noiseData.temperature) &&
                    terrain.data.moisture.match(noiseData.moisture)
        }
        return terrain ?: fallbackTerrain.also {
            logger.debug { "No terrain for ${biome?.data?.name} for $noiseData" }
        }
    }

    /**
     * Creates a fallback terrain for when no matching terrain is found
     */
    private fun createFallbackTerrain(): Terrain {
        return Biome(
            BiomeData(
                "Fallback",
                terrains = listOf(
                    BiomeData.TerrainData("Fallback")
                ),
                color = Color.MAGENTA
            ),
            data.tileSize
        ).terrains.first()
    }


    internal fun AutoDisposable.readBiomes(biomes: List<BiomeData>, tileSize: Int): List<Biome> =
        biomes.map {
            Biome(it, tileSize).apply {
                atlas?.let(disposables::add)
            }.disposable()
        }

}