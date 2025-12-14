package org.roldy.rendering.screen.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.Vector2Int
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.logger
import org.roldy.data.tile.RoadTileData
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.item.SpriteTileObject
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator
import kotlin.math.absoluteValue


class RoadsPopulator(
    override val map: WorldMap,
    private val roads: List<RoadTileData>
) : AutoDisposableAdapter(), WorldChunkPopulator {
    val logger by logger()

    val atlas = AtlasLoader.roads.disposable()

    // Cache available variants for each bitmask
    private val variantCache = mutableMapOf<String, List<Int>>()

    init {
        // Pre-populate variant cache from atlas
        atlas.regions.forEach { region ->
            val match = Regex("hexRoad-(\\d{6})-(\\d{2})").find(region.name)
            match?.let {
                val bitmask = it.groupValues[1]
                val variant = it.groupValues[2].toInt()

                variantCache.getOrPut(bitmask) { mutableListOf<Int>() }
                    .let { list ->
                        if (list is MutableList && variant !in list) {
                            list.add(variant)
                        }
                    }
            }
        }

        // Sort variant lists
        variantCache.values.forEach { (it as? MutableList)?.sort() }
        logger.debug("Loaded road variants for ${variantCache.size} bitmasks")
    }

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val chunkData = chunk.terrainData()
        val roadsInChunk = roads.filter { road ->
            chunkData.contains(road.node.coords)
        }

        return roadsInChunk.map { road ->
            val position = worldPosition(road.node.coords)
            SpriteTileObject.Data(
                position = position,
                coords = road.node.coords,
                textureRegion = getRoadTexture(road.bitmask, road.node.coords),
                layer = Layered.LAYER_1,
                data = mapOf("" to road)
            )
        }
    }

    /**
     * Gets texture region for road tile based on connections.
     * Uses bitmask naming: hexRoad-{6-bit}-{2-digit-variant}
     */
    private fun getRoadTexture(bitmask: String, coords: Vector2Int): TextureRegion {
        val availableVariants = getAvailableVariants(bitmask)
        val variant = pickVariant(availableVariants, coords)

        val regionName = "hexRoad-$bitmask-${variant.toString().padStart(2, '0')}"

        return atlas.findRegion(regionName)
            ?: atlas.findRegion("hexRoad-000000-00")
            ?: error("No fallback road texture found in atlas")
    }

    /**
     * Gets list of available variant numbers for a bitmask.
     * Results are cached after first lookup.
     */
    private fun getAvailableVariants(bitmask: String): List<Int> {
        return variantCache.getOrPut(bitmask) {
            val variants = mutableListOf<Int>()

            // Check variants 00 through 99
            for (i in 0..99) {
                val variantStr = i.toString().padStart(2, '0')
                val regionName = "hexRoad-$bitmask-$variantStr"

                if (atlas.findRegion(regionName) != null) {
                    variants.add(i)
                } else if (variants.isNotEmpty()) {
                    // Stop checking once we hit a gap (variants are sequential)
                    break
                }
            }

            // If no variants found, return [0] as fallback
            if (variants.isEmpty()) {
                println("Warning: No variants found for bitmask $bitmask")
                listOf(0)
            } else {
                variants
            }
        }
    }

    /**
     * Picks a variant deterministically based on coordinates.
     * Ensures consistent variation across the map.
     */
    private fun pickVariant(availableVariants: List<Int>, coords: Vector2Int): Int {
        if (availableVariants.isEmpty()) return 0
        if (availableVariants.size == 1) return availableVariants[0]

        // Hash coordinates to pick variant
        val hash = (coords.x * 73856093) xor (coords.y * 19349663)
        return availableVariants[hash.absoluteValue % availableVariants.size]
    }


}