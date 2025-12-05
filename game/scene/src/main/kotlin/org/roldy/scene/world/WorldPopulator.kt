package org.roldy.scene.world

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.renderer.chunk.ChunkObjectData
import org.roldy.core.renderer.chunk.ChunkPopulator
import org.roldy.map.WorldMapChunk
import org.roldy.terrain.biome.Terrain

class WorldPopulator(
    private val atlas: TextureAtlas,
    private val terrainData: Map<Vector2Int, Terrain>
) : ChunkPopulator<WorldMapChunk> {
    val logger by logger("WorldPopulator")
    private val biomeSprites = mapOf(
        "Forest" to "tile000",
        "Swamp" to "tile041",
        "Desert" to "tile025",
        "DeepDesert" to "tile105",
        "Ice" to "tile096",
        "Jungle" to "tile081",
        "Snow" to "tile073",
        "Savanna" to "tile064",
    )

    override fun populate(
        chunk: WorldMapChunk
    ): List<ChunkObjectData> =
        mutableListOf<ChunkObjectData>().apply {
            val data = terrainData.filterBy(chunk)

            this += data.map { (position, terrain) ->
                fun rnd() = Math.random().toFloat()
                val x = position.x * chunk.tileSize + rnd() * (chunk.tileSize / 2)
                val y = position.y * chunk.tileSize + rnd() * (chunk.tileSize / 2)
                ChunkObjectData(region = biomeSprites[terrain.biome.data.name]?:"", x = x, y = y)
            }
        }

//    private fun Vector2Int.worldPosition(): Vector2Int {
//
//    }

    private fun Map<Vector2Int, Terrain>.filterBy(
        chunk: WorldMapChunk
    ) = run {
        chunk.tilesCoords.mapNotNull { coords ->
            this[coords]?.let {
                coords to it
            }
        }.toMap()
    }
}