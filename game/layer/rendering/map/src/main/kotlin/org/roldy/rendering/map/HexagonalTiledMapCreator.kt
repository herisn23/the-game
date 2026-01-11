package org.roldy.rendering.map

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import org.roldy.core.Vector2Int
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.configuration.match
import org.roldy.data.map.MapData
import org.roldy.data.map.NoiseData
import org.roldy.data.tile.TileData
const val tileSize = 256

data class MapTerrainData(
    val terrain: Terrain,
    val noiseData: NoiseData,
    override val coords: Vector2Int
) : TileData {
    override val walkCost: Float = terrain.data.walkCost ?: terrain.biome.data.walkCost
}

class HexagonalTiledMapCreator(
    val data: MapData,
    val noiseData: Map<Vector2Int, NoiseData>,
    val tilesAtlas: TextureAtlas,
    val biomes: List<Biome>,
    val colorTextures: Map<BiomeType, Texture>,
    val generateColorsLayer: Boolean = false
) {

    // Cache for terrain at each position
    private val terrainCache = mutableMapOf<Vector2Int, MapTerrainData>()

    /**
     * Generates a complete TiledMap with biomes and optional transitions
     */
    fun create(): Pair<TiledMap, Map<Vector2Int, MapTerrainData>> {
        val tiledMap = TiledMap()
        // Generate base terrain layer
        val baseLayer = generateBaseLayer(biomes)

        tiledMap.layers.add(baseLayer)

        if (generateColorsLayer)
        // Generate colors layer
            tiledMap.layers.add(generateBiomeLayer())
        return tiledMap to terrainCache
    }

    private fun generateBiomeLayer(): TiledMapTileLayer {
        val layer = TiledMapTileLayer(data.size.width, data.size.height, tileSize, tileSize)
        terrainCache.forEach { (coords, terrain) ->
            val cell = TiledMapTileLayer.Cell().apply {
                tile = StaticTiledMapTile(TextureRegion(colorTextures[terrain.terrain.biome.data.type]))
            }
            layer.setCell(coords.x, coords.y, cell)
        }

        return layer
    }

    /**
     * Generates the base terrain layer without transitions
     */
    private fun generateBaseLayer(biomes: List<Biome>): TiledMapTileLayer {
        val layer = TiledMapTileLayer(data.size.width, data.size.height, tileSize, tileSize)
        noiseData.forEach { (coords, noiseData) ->
            val terrain = findTerrainForNoise(biomes, noiseData)

            // Cache the terrain for transition calculations
            terrainCache[coords] = MapTerrainData(
                terrain,
                noiseData,
                coords
            )

            // Create cell with the selected terrain2
            val cell = TiledMapTileLayer.Cell().apply {
                tile = StaticTiledMapTile(runCatching {
                    tilesAtlas[terrain.data.name]
                }.onFailure {
                    println("Failed to find texture ${terrain.data.name}")
                }.getOrThrow())

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
            biome.data.match(noiseData)
        }

        // Find matching terrain within biome
        val terrain = biome?.terrains?.find { terrain ->
            terrain.data.match(noiseData)
        }
        return terrain ?: error("No ${biome?.data?.type} terrain for $noiseData")
    }

}