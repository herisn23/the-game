package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.pathwalker.PathWalker
import org.roldy.core.plus
import org.roldy.core.utils.hexDistance
import org.roldy.data.state.SettlementState
import org.roldy.data.tile.HexEdgeDirection
import org.roldy.data.tile.RoadTileData
import org.roldy.data.tile.connectionsToBitmask
import org.roldy.gp.world.generator.road.*
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.rendering.map.WorldMap
import kotlin.random.Random

class RoadGenerator(
    val map: WorldMap,
    val settlements: List<SettlementState>,
    val algorithm: Algorithm = Algorithm.K_NEAREST,
    override val occupied: (Vector2Int) -> Boolean
) : WorldGenerator<RoadTileData> {
    private val roadOffset = Vector2Int(0, 0)
    private val logger by logger()
    val pathfinder = TilePathfinder(map, ::baseCost)

    /**
     *  calculate walkCost from terrainData and add some noise to make roads more natural(not so straight)
     *  also check when tile is occupied
     */
    fun baseCost(tile: Vector2Int, goal: Vector2Int) =
        map.terrainData.getValue(tile).walkCost *
                Random(map.data.seed + tile.sum + goal.sum).nextFloat() *
                (0f.takeIf { occupied(tile) && !settlements.any { it.coords == tile } } ?: 1f)

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

        fun generate(seed: Long, settlements: List<SettlementState>) =
            alg.generate(seed, settlements, config)
    }

    /**
     * Generates road network using specified algorithm.
     */
    override fun generate(): List<RoadTileData> {
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
        }.apply {
            logger.debug {
                "Generated roads: $size"
            }
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
        source: SettlementState,
        destinations: List<SettlementState>,
        existingRoadsNodes: Set<PathWalker.Node>
    ): Set<PathWalker.Node> {
        if (destinations.isEmpty()) return emptySet()

        val allRoads = mutableSetOf<PathWalker.Node>()
        val remainingDests = destinations.toMutableList()

        fun Vector2Int.node() =
            PathWalker.Node(this, map.tilePosition.resolve(this))

        // Include existing roads in our network from the start
        allRoads.addAll(existingRoadsNodes)

        // Helper to check if a settlement is already connected
        fun isConnectedToRoadNetwork(settlement: SettlementState): Boolean {
            val roadEntry = settlement.coords + roadOffset
            val existingCoords = allRoads.map { it.coords }.toSet()
            return settlement.coords in existingCoords || roadEntry in existingCoords
        }

        // Calculate road entry points for source
        val sourceRoadEntry = source.coords + roadOffset

        // Filter out destinations that are already connected
        val unconnectedDests = remainingDests.filter { !isConnectedToRoadNetwork(it) }.toMutableList()

        if (unconnectedDests.isEmpty()) {
            // All destinations already connected, nothing to do
            return allRoads
        }

        val firstDest = unconnectedDests.removeAt(0)
        val firstDestRoadEntry = firstDest.coords + roadOffset

        // Only build main path if source isn't already connected
        if (!isConnectedToRoadNetwork(source)) {
            val mainPath = pathfinder.findPath(sourceRoadEntry, firstDestRoadEntry)
            if (mainPath.isComplete) {
                allRoads.add(source.coords.node())
                allRoads.addAll(mainPath.tiles)
            }
        } else {
            // Source is connected, just path from network to first destination
            val bestBranchPoint = allRoads.minByOrNull { roadTile ->
                hexDistance(roadTile.coords, firstDestRoadEntry)
            }?.coords ?: sourceRoadEntry

            val pathToFirst = pathfinder.findPath(bestBranchPoint, firstDestRoadEntry)
            if (pathToFirst.isComplete) {
                allRoads.add(bestBranchPoint.node())
                allRoads.addAll(pathToFirst.tiles)
            }
        }

        // For remaining unconnected destinations, branch from existing roads
        unconnectedDests.forEach { dest ->
            val destRoadEntry = dest.coords + roadOffset

            val pathfinder = TilePathfinder(map) { tile, goal ->
                val baseCost = map.terrainData.getValue(tile).walkCost *
                        Random(map.data.seed + tile.sum + goal.sum).nextFloat() *
                        (0f.takeIf { occupied(tile) && !settlements.any { it.coords == tile } } ?: 1f)

                // Existing roads are nearly free
                if (tile in allRoads.map { it.coords }) {
                    baseCost * 0.001f
                } else {
                    baseCost
                }
            }

            // Find closest point on existing roads to this destination
            val bestBranchPoint = allRoads.minByOrNull { roadTile ->
                hexDistance(roadTile.coords, destRoadEntry)
            }?.coords ?: sourceRoadEntry

            // Path from branch point to destination road entry
            val branchPath = pathfinder.findPath(bestBranchPoint, destRoadEntry)
            if (branchPath.isComplete) {
                allRoads.add(bestBranchPoint.node())
                allRoads.addAll(branchPath.tiles)
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
        return coords + direction.getOffset(!map.isStaggered(coords))
    }
}

