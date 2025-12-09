package org.roldy.scene.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.roldy.core.Vector2Int
import org.roldy.core.asset.loadAsset
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.pathwalker.PathWalker
import org.roldy.core.plus
import org.roldy.core.x
import org.roldy.environment.TileObject
import org.roldy.environment.item.SpriteTileObject
import org.roldy.map.WorldMap
import org.roldy.scene.world.chunk.WorldMapChunk
import org.roldy.scene.world.pathfinding.Pathfinder
import org.roldy.scene.world.populator.WorldChunkPopulator
import kotlin.math.abs
import kotlin.random.Random

class RoadsPopulator(
    override val map: WorldMap,
    pathfinder: Pathfinder,
    private val settlements: List<SettlementData>,
) : AutoDisposableAdapter(), WorldChunkPopulator {

    private val staggerAxis = map.staggerAxis
    private val staggerIndex = map.staggerIndex

    val roadsPositions = generateRoadNetwork(
        settlements = settlements,
        pathfinder = pathfinder,
        algorithm = RoadNetworkAlgorithm.K_NEAREST,
        k = 3
    )

    val atlas by disposable { TextureAtlas(loadAsset("environment/Roads.atlas")) }

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
            val roadVariant = determineRoadVariant(connections)

            SpriteTileObject.Data(
                name = "road",
                position = position,
                coords = road.coords,
                textureRegion = getRoadTexture(roadVariant, road.coords)
            )
        }
    }

    private fun generateRoadNetwork(
        settlements: List<SettlementData>,
        pathfinder: Pathfinder,
        algorithm: RoadNetworkAlgorithm,
        k: Int = 3
    ): List<PathWalker.PathNode> {
        if (settlements.size < 2) return emptyList()

        val edges = when (algorithm) {
            RoadNetworkAlgorithm.MST -> buildMinimumSpanningTree(settlements)
            RoadNetworkAlgorithm.K_NEAREST -> buildKNearestNetwork(settlements, k)
        }

        val allPaths = mutableListOf<PathWalker.PathNode>()
        edges.forEach { (from, to) ->
            val path = pathfinder.findPath(from.coords, to.coords)
            if (path.isComplete) {
                allPaths.addAll(path.tiles)
            }
        }

        val settlementCoords = settlements.map { it.coords }.toSet()
        return allPaths
            .distinctBy { it.coords }
            .filterNot { it.coords in settlementCoords }
    }

    private fun buildMinimumSpanningTree(
        settlements: List<SettlementData>
    ): List<Pair<SettlementData, SettlementData>> {
        val edges = mutableListOf<Pair<SettlementData, SettlementData>>()
        val visited = mutableSetOf<SettlementData>()

        visited.add(settlements.first())

        while (visited.size < settlements.size) {
            var minDistance = Float.MAX_VALUE
            var closestEdge: Pair<SettlementData, SettlementData>? = null

            visited.forEach { from ->
                settlements.filter { it !in visited }.forEach { to ->
                    val distance = hexDistance(from.coords, to.coords)
                    if (distance < minDistance) {
                        minDistance = distance
                        closestEdge = from to to
                    }
                }
            }

            closestEdge?.let { (from, to) ->
                edges.add(from to to)
                visited.add(to)
            }
        }

        return edges
    }

    private fun buildKNearestNetwork(
        settlements: List<SettlementData>,
        k: Int
    ): List<Pair<SettlementData, SettlementData>> {
        val edges = mutableListOf<Pair<SettlementData, SettlementData>>()
        val processedPairs = mutableSetOf<Set<SettlementData>>()

        settlements.forEach { from ->
            val nearest = settlements
                .filter { it != from }
                .sortedBy { to -> hexDistance(from.coords, to.coords) }
                .take(k)

            nearest.forEach { to ->
                val pair = setOf(from, to)
                if (pair !in processedPairs) {
                    processedPairs.add(pair)
                    edges.add(from to to)
                }
            }
        }

        return edges
    }

    private fun getConnectedDirections(
        coords: Vector2Int,
        roadCoords: Set<Vector2Int>
    ): List<HexEdgeDirection> {
        return HexEdgeDirection.values().filter { direction ->
            val neighbor = getHexNeighbor(coords, direction)
            neighbor in roadCoords
        }
    }

    private fun getHexNeighbor(coords: Vector2Int, direction: HexEdgeDirection): Vector2Int {
        if (staggerAxis != "y") {
            return coords + direction.getOffset(false)
        }

        val isStaggered = if (staggerIndex == "even") {
            coords.y % 2 == 0
        } else {
            coords.y % 2 == 1
        }

        return coords + direction.getOffset(isStaggered)
    }

    /**
     * Determines the specific road variant based on connections.
     */
    private fun determineRoadVariant(connections: List<HexEdgeDirection>): RoadVariant {
        return when (connections.size) {
            0 -> RoadVariant.Isolated
            1 -> RoadVariant.End(connections[0])
            2 -> {
                val dir1 = connections[0]
                val dir2 = connections[1]
                if (areOpposite(dir1, dir2)) {
                    RoadVariant.Straight(dir1, dir2)
                } else {
                    RoadVariant.Curve(dir1, dir2)
                }
            }
            3 -> RoadVariant.TJunction(connections)
            4 -> RoadVariant.FourWay(connections)
            5 -> RoadVariant.FiveWay(connections)
            6 -> RoadVariant.SixWay
            else -> RoadVariant.Isolated
        }
    }

    private fun areOpposite(dir1: HexEdgeDirection, dir2: HexEdgeDirection): Boolean {
        return when (dir1) {
            HexEdgeDirection.EAST -> dir2 == HexEdgeDirection.WEST
            HexEdgeDirection.WEST -> dir2 == HexEdgeDirection.EAST
            HexEdgeDirection.NORTHEAST -> dir2 == HexEdgeDirection.SOUTHWEST
            HexEdgeDirection.SOUTHWEST -> dir2 == HexEdgeDirection.NORTHEAST
            HexEdgeDirection.NORTHWEST -> dir2 == HexEdgeDirection.SOUTHEAST
            HexEdgeDirection.SOUTHEAST -> dir2 == HexEdgeDirection.NORTHWEST
        }
    }

    /**
     * Maps road variant to texture region name.
     */
    private fun getRoadTexture(variant: RoadVariant, coords: Vector2Int): TextureRegion {
        val regionName = when (variant) {
            // Straight roads (3 variants)
            is RoadVariant.Straight -> when {
                variant.hasDirections(HexEdgeDirection.EAST, HexEdgeDirection.WEST) ->
                    variantOf("w-e", coords, 1,3)
                variant.hasDirections(HexEdgeDirection.NORTHEAST, HexEdgeDirection.SOUTHWEST) ->
                    "hexRoad-straight-ne-sw"
                variant.hasDirections(HexEdgeDirection.NORTHWEST, HexEdgeDirection.SOUTHEAST) ->
                    "hexRoad-straight-nw-se"
                else -> "default"
            }

            // Curves (6 variants for clockwise curves)
            is RoadVariant.Curve -> when {
                variant.hasDirections(HexEdgeDirection.EAST, HexEdgeDirection.NORTHEAST) ->
                    "hexRoad-curve-e-ne"
                variant.hasDirections(HexEdgeDirection.NORTHEAST, HexEdgeDirection.NORTHWEST) ->
                    "hexRoad-curve-ne-nw"
                variant.hasDirections(HexEdgeDirection.NORTHWEST, HexEdgeDirection.WEST) ->
                    "hexRoad-curve-nw-w"
                variant.hasDirections(HexEdgeDirection.WEST, HexEdgeDirection.SOUTHWEST) ->
                    "hexRoad-curve-w-sw"
                variant.hasDirections(HexEdgeDirection.SOUTHWEST, HexEdgeDirection.SOUTHEAST) ->
                    "hexRoad-curve-sw-se"
                variant.hasDirections(HexEdgeDirection.SOUTHEAST, HexEdgeDirection.EAST) ->
                    "hexRoad-curve-se-e"
                else -> "hexRoad-default"
            }

            // T-Junctions (6 variants - gap in each direction)
            is RoadVariant.TJunction -> {
                val missing = HexEdgeDirection.values().first { it !in variant.connections }
                when (missing) {
                    HexEdgeDirection.EAST -> "hexRoad-t-gap-e"
                    HexEdgeDirection.NORTHEAST -> "hexRoad-t-gap-ne"
                    HexEdgeDirection.NORTHWEST -> "hexRoad-t-gap-nw"
                    HexEdgeDirection.WEST -> "hexRoad-t-gap-w"
                    HexEdgeDirection.SOUTHWEST -> "hexRoad-t-gap-sw"
                    HexEdgeDirection.SOUTHEAST -> "hexRoad-t-gap-se"
                }
            }

            // Ends (6 variants - one for each direction)
            is RoadVariant.End -> when (variant.direction) {
                HexEdgeDirection.EAST -> "hexRoad-end-e"
                HexEdgeDirection.NORTHEAST -> "hexRoad-end-ne"
                HexEdgeDirection.NORTHWEST -> "hexRoad-end-nw"
                HexEdgeDirection.WEST -> "hexRoad-end-w"
                HexEdgeDirection.SOUTHWEST -> "hexRoad-end-sw"
                HexEdgeDirection.SOUTHEAST -> "hexRoad-end-se"
            }

            // Intersections
            is RoadVariant.FourWay -> "hexRoad-4way"
            is RoadVariant.FiveWay -> "hexRoad-5way"
            is RoadVariant.SixWay -> "hexRoad-6way"
            is RoadVariant.Isolated -> "hexRoad-default"
        }

        return atlas.findRegion(regionName) ?: atlas.findRegion("default")
    }
    fun variantOf(name:String, coords: Vector2Int, from:Int, to:Int) =
        Random(coords.x + coords.y).nextInt(
            from, to
        ).let {
            "$name-$it"
        }


    private fun hexDistance(a: Vector2Int, b: Vector2Int): Float {
        return (abs(a.x - b.x) + abs(a.y - b.y)).toFloat()
    }
}

/**
 * Specific road variants with their directions.
 */
sealed class RoadVariant {
    object Isolated : RoadVariant()
    data class End(val direction: HexEdgeDirection) : RoadVariant()
    data class Straight(val dir1: HexEdgeDirection, val dir2: HexEdgeDirection) : RoadVariant() {
        fun hasDirections(d1: HexEdgeDirection, d2: HexEdgeDirection): Boolean {
            return (dir1 == d1 && dir2 == d2) || (dir1 == d2 && dir2 == d1)
        }
    }
    data class Curve(val dir1: HexEdgeDirection, val dir2: HexEdgeDirection) : RoadVariant() {
        fun hasDirections(d1: HexEdgeDirection, d2: HexEdgeDirection): Boolean {
            return (dir1 == d1 && dir2 == d2) || (dir1 == d2 && dir2 == d1)
        }
    }
    data class TJunction(val connections: List<HexEdgeDirection>) : RoadVariant()
    data class FourWay(val connections: List<HexEdgeDirection>) : RoadVariant()
    data class FiveWay(val connections: List<HexEdgeDirection>) : RoadVariant()
    object SixWay : RoadVariant()
}

enum class RoadNetworkAlgorithm {
    MST,
    K_NEAREST
}

enum class HexEdgeDirection {
    EAST,
    NORTHEAST,
    NORTHWEST,
    WEST,
    SOUTHWEST,
    SOUTHEAST;

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