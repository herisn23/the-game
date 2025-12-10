package org.roldy.gameplay.world.generator

import org.roldy.core.PathWalker
import org.roldy.core.Vector2Int
import org.roldy.core.pathwalker.Pathfinder
import org.roldy.core.plus
import org.roldy.core.x
import org.roldy.gameplay.world.generator.road.*
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.scene.world.populator.environment.RoadData
import org.roldy.rendering.scene.world.populator.environment.SettlementData
import kotlin.math.abs

class RoadGenerator(
    val map: WorldMap,
    val settlements: List<SettlementData>,
    val pathfinder: Pathfinder
) {
    private val settlementCoords = settlements.map { it.coords }.toSet()
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

        fun generate(seed: Long, settlements: List<SettlementData>) =
            alg.generate(seed, settlements, config)
    }

    /**
     * Generates road network using specified algorithm.
     */
    fun generate(
        algorithm: Algorithm = Algorithm.MST,
    ): List<RoadData> {
        if (settlements.size < 2) return emptyList()

        // Build network edges
        val edges = algorithm.generate(map.data.seed, settlements)

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
            RoadData(it, connectionsToBitmask(connections))
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

/**
 * Calculates hex distance between two coordinates.
 */
fun hexDistance(a: Vector2Int, b: Vector2Int): Float {
    return (abs(a.x - b.x) + abs(a.y - b.y)).toFloat()
}