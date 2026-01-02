package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.pathwalker.PathWalker
import org.roldy.core.plus
import org.roldy.core.utils.hexDistance
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
    val algorithm: Algorithm = Algorithm.K_NEAREST,
    override val occupied: (Vector2Int) -> Boolean
) : WorldGenerator<RoadTileData> {
    private val staggerAxis = map.staggerAxis
    private val staggerIndex = map.staggerIndex
    val pathfinder = TilePathfinder(map, ::baseCost)

    /**
     *  calculate walkCost from terrainData and add some noise to make roads more natural(not so straight)
     *  also check when tile is occupied
     */
    fun baseCost(tile: Vector2Int, goal: Vector2Int) =
        map.terrainData.getValue(tile).walkCost *
                Random(map.data.seed + tile.sum + goal.sum).nextFloat() *
                (0f.takeIf { occupied(tile) && !settlements().any { it.coords == tile } } ?: 1f)

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
            KNearest, mapOf("k" to 3, "randomness" to 0.5f)
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

        val edges = algorithm.generate(map.data.seed + GeneratorSeeds.ROADS_SEED, settlements)

        // Group edges: A -> [B, C, D]
        val edgesBySource = edges.groupBy({ it.first }, { it.second })

        val allRoadTiles = mutableSetOf<PathWalker.Node>()

        edgesBySource.forEach { (source, destinations) ->
            // Build branching network from this source
            val branchingPaths = buildBranchingNetwork(source, destinations, allRoadTiles)
            allRoadTiles.addAll(branchingPaths)
        }

        // Convert to RoadTileData with proper connections
        return allRoadTiles.map { node ->
            val connections = getConnectedDirections(node.coords, allRoadTiles.map { it.coords }.toSet())
            RoadTileData(node, connectionsToBitmask(connections))
        }
    }

    /**
     * Builds a branching road network from source to multiple destinations.
     * Later destinations can branch off from earlier paths.
     */
    /**
     * Builds optimal branching network by finding best branch points.
     */
    private fun buildBranchingNetwork(
        source: SettlementTileData,
        destinations: List<SettlementTileData>,
        existingRoadsNodes: Set<PathWalker.Node>
    ): Set<PathWalker.Node> {
        if (destinations.isEmpty()) return emptySet()

        val allRoads = mutableSetOf<PathWalker.Node>()
        val remainingDests = destinations.toMutableList()

        // Find path to first destination (establishes main trunk)
        val firstDest = remainingDests.removeAt(0)
        val mainPath = pathfinder.findPath(source.coords, firstDest.coords)
        if (mainPath.isComplete) {
            allRoads.addAll(mainPath.tiles)

            // For remaining destinations, branch from any point on existing roads
            remainingDests.forEach { dest ->
                val pathfinder = TilePathfinder(map) { tile, goal ->
                    val baseCost = map.terrainData.getValue(tile).walkCost *
                            Random(map.data.seed + tile.sum + goal.sum).nextFloat() *
                            (0f.takeIf { occupied(tile) && !settlements().any { it.coords == tile } } ?: 1f)

                    // Existing roads and global roads are nearly free
                    if (tile in allRoads.map { it.coords } || tile in mainPath.tiles.map { it.coords }) {
                        baseCost * 0.001f
                    } else {
                        baseCost
                    }
                }

                // Find closest point on existing roads to this destination
                val bestBranchPoint = allRoads.minByOrNull { roadTile ->
                    hexDistance(roadTile.coords, dest.coords)
                }?.coords ?: source.coords

                // Path from branch point to destination
                val branchPath = pathfinder.findPath(bestBranchPoint, dest.coords)
                if (branchPath.isComplete) {
                    allRoads.addAll(branchPath.tiles)
                }
            }
        }

        return allRoads
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

