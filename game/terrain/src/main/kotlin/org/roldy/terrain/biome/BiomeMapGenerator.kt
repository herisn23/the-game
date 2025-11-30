package org.roldy.terrain.biome

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import org.roldy.terrain.SimplexNoise
import kotlin.math.abs

class BiomeMapGenerator(
    seed: Long,
    val width: Int,
    val height: Int,
    val tileSize: Int,
    var elevation: Float = 0.0025f,
    var moisture: Float = 0.005f,
    var temperature: Float = 0.1f
) {

    private val temperatureNoise = SimplexNoise(seed)
    private val moistureNoise = SimplexNoise(seed + 1)
    private val elevationNoise = SimplexNoise(seed + 2)

    val fallbackTerrain = Biome(
        BiomesSettings(
            mapOf(
                "default" to Color.BLACK
            )
        ),
        BiomeData(
            "Default", terrains = listOf(
                BiomeData.TerrainData(
                    "Default", "default"
                )
            )
        ), tileSize
    ).terrains.first()

    fun generate(): TiledMap {
        val tiledMap = TiledMap()
        val layer = TiledMapTileLayer(width, height, tileSize, tileSize)
        val biomes = loadBiomes(tileSize)
        generateMap(layer) {
            findTerrain(biomes) ?: fallbackTerrain
        }
        tiledMap.layers.add(layer)
        return tiledMap
    }


    data class NoiseData(
        val x: Int,
        val y: Int,
        override val elevation: Float,
        override val temperature: Float,
        override val moisture: Float
    ) : HeightData

    private fun generateMap(layer: TiledMapTileLayer, findTerrain: context(NoiseData) () -> Terrain) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                // Generate multiple noise values
                val elevation = getElevation(x, y)
                val temperature = getTemperature(x, y, height)
                val moisture = getMoisture(x, y)
                val noiseData = NoiseData(x, y, elevation, temperature, moisture)
                val cell = context(noiseData) {
                    TiledMapTileLayer.Cell().apply {
                        val terrain = findTerrain()
                        tile = StaticTiledMapTile(terrain.region)
                    }
                }
                layer.setCell(x, y, cell)
            }
        }
    }

    private fun getElevation(x: Int, y: Int): Float {
        var total = 0.0f
        var frequency = elevation
        var amplitude = 1f

        repeat(4) {  // Fewer octaves - was 5
            total += elevationNoise(x * frequency, y * frequency) * amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        return total
    }

    private fun getTemperature(x: Int, y: Int, height: Int): Float {
        // Stronger latitude influence, less noise
        val latitudeFactor = 1.0f - (abs(y - height / 2.0f) / (height / 2.0f))
        val noise = temperatureNoise(x * 0.008f, y * 0.008f)  // Lower - was 0.03
        return latitudeFactor * 0.85f + noise * temperature  // More weight to latitude
    }

    private fun getMoisture(x: Int, y: Int): Float {
        var total = 0.0f
        var amplitude = 1f
        var frequency = moisture

        repeat(3) {
            total += moistureNoise(x * frequency, y * frequency) * amplitude
            frequency *= 2
            amplitude *= 0.5f
        }

        return (total + 1) / 2
    }
}