package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.pathwalker.PathWalker
import org.roldy.core.plus
import org.roldy.core.x
import org.roldy.data.tile.RoadTileData
import org.roldy.data.tile.SettlementTileData
import org.roldy.gp.world.generator.road.*
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.rendering.map.WorldMap
import kotlin.random.Random

class RoadGenerator(
    val map: WorldMap,
    val settlements: () -> List<SettlementTileData>,
    val algorithm: Algorithm = Algorithm.MST,
    override val occupied: (Vector2Int) -> Boolean
) : WorldGenerator<RoadTileData> {
    val pathfinder = TilePathfinder(map) { tile, goal ->
        //calculate walkCost from terrainData and add some noise to make roads more natural(not so straight)
        //also when tile is occupied
        map.terrainData.getValue(tile).walkCost *
                Random(map.data.seed + tile.sum + goal.sum).nextFloat() *
                (0f.takeIf { occupied(tile) } ?: 1f)
    }
    private val staggerAxis = map.staggerAxis
    private val staggerIndex = map.staggerIndex

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

        fun generate(seed: Long, settlements: List<SettlementTileData>) =
            alg.generate(seed, settlements, config)
    }

    /**
     * Generates road network using specified algorithm.
     */
    override fun generate(): List<RoadTileData> {
        val settlements = settlements()
        if (settlements.size < 2) return emptyList()

        // Build network edges
        val edges = algorithm.generate(map.data.seed + GeneratorSeeds.ROADS_SEED, settlements)

        // Calculate paths for all edges
        val allPaths = mutableListOf<PathWalker.Node>()
        edges.forEach { (from, to) ->
            val path = pathfinder.findPath(from.coords, to.coords)
            if (path.isComplete) {
                allPaths.addAll(path.tiles)
            }
        }
        val roads = allPaths.distinctBy { it.coords }
        val roadCoordsSet = roads.map { it.coords }.toSet()
        // Remove duplicates, optionally filter settlements
        return roads.map {
            val connections = getConnectedDirections(it.coords, roadCoordsSet)
            RoadTileData(it, connectionsToBitmask(connections))
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

