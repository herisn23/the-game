package org.roldy.terrain.shader

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile

object ProceduralTerrainGenerator {

//    val atlas by lazy {
//        TextureAtlas("terrain/TileMap.atlas")
//    }

    class TileConfig(
        val name: String,
        val maxHeight: Float,
        texture: () -> Texture
    ) {
        val texture by lazy {
            println("Generating texture for $name")
            texture()
        }
    }

    // Define terrain types that need edge tiles
    context(_:Int)
    val terrainConfiguration get() = listOf(
        TileConfig("Ocean", 0.1f) {
            createTileTexture(Color.valueOf("001a4d"))
        },
        TileConfig("DeepWater", 0.35f) {
            createTileTexture(Color.valueOf("003d7a"))
        },
        TileConfig("ShallowWater", 0.4f) {
            createTileTexture(Color.valueOf("1e5a8f"))
        },
        TileConfig("Shore", 0.42f) {
            createTileTexture(Color.valueOf("4a8fb3"))
        },
        TileConfig("Beach", 0.45f) {
            createTileTexture(Color.valueOf("f0e68c"))
        },
        TileConfig("Grass", 0.5f) {
            createTileTexture(Color.valueOf("7ec850"))
        },
        TileConfig("GrassLand", 0.6f) {
            createTileTexture(Color.valueOf("5da832"))
        },
        TileConfig("Forest", 0.7f) {
            createTileTexture(Color.valueOf("3d7a1f"))
        },
        TileConfig("Hills", 0.8f) {
            createTileTexture(Color.valueOf("8b7355"))
        },
        TileConfig("Mountains", 0.9f) {
            createTileTexture(Color.valueOf("6b6b6b"))
        },
        TileConfig("Peak", 1.0f) {
            createTileTexture(Color.valueOf("f5f5f5"))
        }
    )

    context(tileSize:Int)
    private fun createTileTexture(color: Color) =
        Pixmap(tileSize, tileSize, Pixmap.Format.RGBA8888).run {
            setColor(color)
            fill()
            Texture(this)
        }

    fun generateTerrain(width: Int, height: Int, tileSize: Int): TiledMap {
        val tiledMap = TiledMap()

        // Create base terrain layer
        val baseLayer = TiledMapTileLayer(width, height, tileSize, tileSize)
        baseLayer.name = "Base Terrain"

        // Create transition layer
        val transitionLayer = TiledMapTileLayer(width, height, tileSize, tileSize)
        transitionLayer.name = "Transitions"

        val terrainTextures = with(tileSize) {
            terrainConfiguration
        }

        fun findTerrainTile(height: Float): TileConfig =
            terrainTextures.first { config ->
                height <= config.maxHeight
            }
        val noise = SimplexNoise(1)

        // First pass: Generate base terrain
        for (x in 0 until width) {
            for (y in 0 until height) {
                val nx = x / width.toFloat()
                val ny = y / height.toFloat()
                val heightValue = (noise(nx, ny) + 1f) / 2f

                val tileConfig = findTerrainTile(heightValue)

                val cell = TiledMapTileLayer.Cell()
                cell.tile = StaticTiledMapTile(TextureRegion(tileConfig.texture))
                baseLayer.setCell(x, y, cell)
            }
        }

        tiledMap.layers.add(baseLayer)
        return tiledMap.apply {
            properties.put("orientation", "hexagonal");
            properties.put("staggeraxis", "x"); // or "y"
            properties.put("staggerindex", "odd"); // or "even"
        }
    }
}