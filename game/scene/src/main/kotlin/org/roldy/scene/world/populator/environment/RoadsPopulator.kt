package org.roldy.scene.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.Vector2Int
import org.roldy.core.asset.loadAsset
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.pathwalker.PathWalker
import org.roldy.core.plus
import org.roldy.core.renderer.Layered
import org.roldy.core.x
import org.roldy.environment.TileObject
import org.roldy.environment.item.SpriteTileObject
import org.roldy.map.WorldMap
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.pathfinding.Pathfinder
import org.roldy.scene.world.populator.WorldChunkPopulator
import org.roldy.scene.world.populator.environment.road.*
import kotlin.math.abs
import kotlin.math.absoluteValue

class RoadsPopulator(
    override val map: WorldMap,
    pathfinder: Pathfinder,
    private val settlements: List<SettlementData>,
    private val includeSettlementTiles: Boolean = true  // Set false to create dead ends
) : AutoDisposableAdapter(), WorldChunkPopulator {

    enum class Algorithm(
        val alg: RoadNetworkAlgorithm,
        val config: Map<String, Any> = emptyMap()
    ) {
        HIERARCHICAL(
            Hierarchical,
            mapOf("tierLevels" to 3, "randomness" to 0.3f)
        ),
        WEIGHTED(
            Weighted,
            mapOf("getImportance" to WeightedImportance { it.coords.sum })
        ),
        HUB_AND_SPOKE(
            HubAndSpoke,
            mapOf("hubCount" to 3)
        ),
        DELAUNAY(
            Delaunay,
            mapOf("pruningFactor" to 0.3f)
        ),
        MST(
            MinimumSpanningTree,
            mapOf("randomness" to 0.9f)
        ),
        K_NEAREST(
            KNearest, mapOf("k" to 3)
        );

        fun generate(seed: Long, settlements: List<SettlementData>) =
            alg.generate(seed, settlements, config)
    }

    private val staggerAxis = map.staggerAxis
    private val staggerIndex = map.staggerIndex

    private val settlementCoords = settlements.map { it.coords }.toSet()

    val roadsPositions = generateRoadNetwork(
        settlements = settlements,
        pathfinder = pathfinder,
        algorithm = Algorithm.MST,
    )

    val atlas by disposable { TextureAtlas(loadAsset("environment/Roads.atlas")) }

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

        println("Loaded road variants for ${variantCache.size} bitmasks")
    }

    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val chunkData = chunk.data()
        val roadCoordsSet = roadsPositions.map { it.coords }.toSet()

        val roadsInChunk = roadsPositions.filter { road ->
            chunkData.contains(road.coords)
        }

        return roadsInChunk.map { road ->
            val position = worldPosition(road.coords)
            val connections = getConnectedDirections(road.coords, roadCoordsSet)
            val isSettlement = road.coords in settlementCoords

            SpriteTileObject.Data(
                name = if (isSettlement) "road-settlement" else "road",
                position = position,
                coords = road.coords,
                textureRegion = getRoadTexture(connections, road.coords),
                layer = Layered.LAYER_1
            )
        }
    }

    /**
     * Generates road network using specified algorithm.
     */
    private fun generateRoadNetwork(
        settlements: List<SettlementData>,
        pathfinder: Pathfinder,
        algorithm: Algorithm
    ): List<PathWalker.PathNode> {
        if (settlements.size < 2) return emptyList()

        // Build network edges
        val edges = algorithm.generate(map.seed, settlements)

        // Calculate paths for all edges
        val allPaths = mutableListOf<PathWalker.PathNode>()
        edges.forEach { (from, to) ->
            val path = pathfinder.findPath(from.coords, to.coords)
            if (path.isComplete) {
                allPaths.addAll(path.tiles)
            }
        }

        // Remove duplicates, optionally filter settlements
        val result = allPaths.distinctBy { it.coords }

        return if (includeSettlementTiles) {
            result
        } else {
            result.filterNot { it.coords in settlementCoords }
        }
    }


    /**
     * Get all hex directions that have roads connected to this tile.
     */
    private fun getConnectedDirections(
        coords: Vector2Int,
        roadCoords: Set<Vector2Int>
    ): List<HexEdgeDirection> {
        return HexEdgeDirection.entries.filter { direction ->
            val neighbor = getHexNeighbor(coords, direction)
            neighbor in roadCoords
        }
    }

    /**
     * Gets the neighboring hex coordinate in the given direction.
     * Accounts for stagger offset on flat-top hexagons.
     */
    private fun getHexNeighbor(coords: Vector2Int, direction: HexEdgeDirection): Vector2Int {
        if (staggerAxis != "y") {
            // Handle x-axis stagger if needed
            return coords + direction.getOffset(false)
        }

        // Y-axis stagger (flat-top hexagons)
        val isStaggered = if (staggerIndex == "even") {
            coords.y % 2 == 0
        } else {
            coords.y % 2 == 1
        }

        return coords + direction.getOffset(isStaggered)
    }

    /**
     * Gets texture region for road tile based on connections.
     * Uses bitmask naming: hexRoad-{6-bit}-{2-digit-variant}
     */
    private fun getRoadTexture(connections: List<HexEdgeDirection>, coords: Vector2Int): TextureRegion {
        val bitmask = connectionsToBitmask(connections)

        // DEBUG: Print what we're looking for
        println("Coords $coords: Connections = ${connections.map { it.name }} → Bitmask = $bitmask")

        val availableVariants = getAvailableVariants(bitmask)
        val variant = pickVariant(availableVariants, coords)

        val regionName = "hexRoad-$bitmask-${variant.toString().padStart(2, '0')}"

        return atlas.findRegion(regionName)
            ?: atlas.findRegion("hexRoad-000000-00")
            ?: error("No fallback road texture found in atlas")
    }

    /**
     * Converts list of connected directions to 6-bit binary string.
     * Bit order per asset documentation: NORTHWEST, NORTHEAST, EAST, SOUTHEAST, SOUTHWEST, WEST
     *
     * Bit positions (right to left, 0-5):
     * Bit 0: WEST
     * Bit 1: SOUTHWEST
     * Bit 2: SOUTHEAST
     * Bit 3: EAST
     * Bit 4: NORTHEAST
     * Bit 5: NORTHWEST
     *
     * Examples:
     * - EAST + WEST = 001001
     * - WEST + EAST + SOUTHWEST = 001011
     */
    private fun connectionsToBitmask(connections: List<HexEdgeDirection>): String {
        var mask = 0

        connections.forEach { dir ->
            val bitPosition = when (dir) {
                HexEdgeDirection.WEST -> 0
                HexEdgeDirection.SOUTHWEST -> 1
                HexEdgeDirection.SOUTHEAST -> 2
                HexEdgeDirection.EAST -> 3
                HexEdgeDirection.NORTHEAST -> 4
                HexEdgeDirection.NORTHWEST -> 5
            }
            mask = mask or (1 shl bitPosition)
        }

        return mask.toString(2).padStart(6, '0')
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

/**
 * Hexagonal edge-to-edge directions for flat-top hexagons.
 */
enum class HexEdgeDirection {
    EAST,
    NORTHEAST,
    NORTHWEST,
    WEST,
    SOUTHWEST,
    SOUTHEAST;

    /**
     * Get offset for this direction based on stagger.
     * For Y-axis stagger (flat-top hexagons).
     */
    fun getOffset(isStaggered: Boolean): Vector2Int {
        return when (this) {
            EAST -> 1 x 0
            WEST -> -1 x 0
            NORTHEAST -> if (isStaggered) 0 x 1 else 1 x 1
            NORTHWEST -> if (isStaggered) -1 x 1 else 0 x 1
            SOUTHEAST -> if (isStaggered) 0 x -1 else 1 x -1
            SOUTHWEST -> if (isStaggered) -1 x -1 else 0 x -1
        }
    }
}

/**
 * Calculates hex distance between two coordinates.
 */
fun hexDistance(a: Vector2Int, b: Vector2Int): Float {
    return (abs(a.x - b.x) + abs(a.y - b.y)).toFloat()
}