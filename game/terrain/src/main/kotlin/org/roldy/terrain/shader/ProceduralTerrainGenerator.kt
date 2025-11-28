package org.roldy.terrain.shader

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile

object ProceduralTerrainGenerator {

    fun generateTerrain(width: Int, height: Int): TiledMap {
        val atlas = TextureAtlas("terrain/TileMap.atlas")
        val tiledMap = TiledMap()
        val tileSize = atlas.regions.first().regionWidth
        val layer = TiledMapTileLayer(width, height, tileSize, tileSize)

        // Load terrain textures
        val grassTexture = atlas.findRegion("Grass")
        val sandTexture = atlas.findRegion("Sandy_Rock_Surface")
        val rockTexture = atlas.findRegion("Rocky_Dirt")
        val waterTexture = atlas.findRegion("Water")

        // Create tiles
        val grassTile = StaticTiledMapTile(grassTexture)
        val sandTile = StaticTiledMapTile(sandTexture)
        val rockTile = StaticTiledMapTile(rockTexture)
        val waterTile = StaticTiledMapTile(waterTexture)

        val noise = SimplexNoise()

        // Generate terrain using noise
        for (x in 0 until width) {
            for (y in 0 until height) {
                val nx = x / width.toFloat()
                val ny = y / height.toFloat()

                // Generate height value
                val heightValue = (noise.octaveNoise(nx * 5f, ny * 5f, 4) + 1f) / 2f

                // Choose tile based on height
                val tile = when {
                    heightValue < 0.3f -> waterTile
                    heightValue < 0.5f -> sandTile
                    heightValue < 0.7f -> grassTile
                    else -> rockTile
                }

                val cell = TiledMapTileLayer.Cell()
                cell.tile = tile
                layer.setCell(x, y, cell)
            }
        }

        tiledMap.layers.add(layer)
        return tiledMap
    }
}