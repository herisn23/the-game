package org.roldy.gp.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.utils.repeat
import org.roldy.core.x
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.configuration.harvestable.HarvestableConfiguration
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.data.mine.HarvestableType
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.data.tile.HarvestableTileData
import org.roldy.data.tile.SettlementTileData
import org.roldy.rendering.map.MapTerrainData
import kotlin.random.Random

class HarvestableGenerator(
    val terrainData: Map<Vector2Int, MapTerrainData>,
    val mapData: MapData,
    val settlements: () -> List<SettlementTileData>,
    harvestableConfiguration: HarvestableConfiguration,
    override val occupied: (Vector2Int) -> Boolean
) : WorldGenerator<HarvestableTileData> {
    val rules = harvestableConfiguration.rules
    val logger by logger()
    val allHarvestable by lazy { HarvestableType.harvestable }

    override fun generate(): List<HarvestableTileData> =
        (generateHarvestableInSettlements() + generateMinesOutsideSettlements()).apply {
            logger.debug {
                "Generated harvestable: $size"
            }
        }

    private fun generateHarvestableInSettlements(): MutableList<HarvestableTileData> {
        val mines = mutableListOf<HarvestableTileData>()
        settlements().forEach { settlement ->
            val radius = settlement.claims.toMutableSet()
            val settlementMines = mutableListOf<HarvestableTileData>()
            repeat(settlement.harvestableCount) { i ->
                val random = Random(mapData.seed + settlement.coords.sum + i)
                val minePosition = radius.random(random).also(radius::remove)
                val harvestable =
                    pickHarvestableByBiome(random, minePosition, settlementMines.map(HarvestableTileData::harvestable))
                harvestable?.let {
                    HarvestableTileData(
                        coords = minePosition,
                        harvestable = harvestable,
                        settlementData = settlement
                    ).let(settlementMines::add)
                }
            }
            logger.debug {
                val tabs = "\t" repeat 5
                val minesStr = settlementMines.joinToString("\n$tabs") {
                    """${it.coords}: ${it.harvestable}"""
                }
                """
                    Mines for settlement: ${settlement.coords}
                      Mines should have: ${settlement.harvestableCount}
                      Mines have: ${settlementMines.size}
                        $minesStr
                """
            }
            mines.addAll(settlementMines)
        }
        return mines
    }

    private fun generateMinesOutsideSettlements(): MutableList<HarvestableTileData> {
        val mines = mutableListOf<HarvestableTileData>()
        val claimedCoords = mutableSetOf<Vector2Int>()
        val maxMines = when (mapData.size) {
            MapSize.Debug -> 1
            MapSize.ExtraLarge -> 2000
            MapSize.Large -> 1500
            MapSize.Medium -> 1000
            MapSize.Small -> 500
        }
        val attempts = maxMines * 10
        val regionsHexes = settlements().flatMap { it.claims }
        val rngX = Random(mapData.seed + GeneratorSeeds.MINES_SEED + 1)
        val rngY = Random(mapData.seed + GeneratorSeeds.MINES_SEED + 2)

        repeat(attempts) {
            if (mines.size >= maxMines) return@repeat

            val coords = tryFindCoords(rngX, rngY)
            val biome = terrainData.getValue(coords).terrain.biome.data.type

            val harvestRandom = Random(mapData.seed + GeneratorSeeds.MINES_SEED + coords.sum)
            val terrainData = terrainData.getValue(coords)

            if (coords !in regionsHexes && coords !in claimedCoords) {
                val harvestable = allHarvestable.filter(filterByBiome(biome)).random(harvestRandom)
                if (isSuitableForMine(terrainData)) {
                    claimedCoords.add(coords)
                    mines.add(
                        HarvestableTileData(
                            coords,
                            harvestable,
                            null
                        )
                    )
                }
            }
        }
        return mines
    }

    fun tryFindCoords(randomX: Random, randomY: Random): Vector2Int {
        //TODO this function maybe need to be improved because can lead to infinite recursion
        val x = randomX.nextInt(mapData.size.width)
        val y = randomY.nextInt(mapData.size.height)
        val coords = x x y
        val biome = terrainData.getValue(coords).terrain.biome.data.type
        if (biome == BiomeType.Water || occupied(coords)) {
            return tryFindCoords(randomX, randomY)
        }
        return coords
    }


    private fun pickHarvestableByBiome(random: Random, coords: Vector2Int, existing: List<Harvestable>): Harvestable? {
        val terrain = terrainData.getValue(coords)
        if (!isSuitableForMine(terrain)) return null

        val biome = terrain.terrain.biome.data.type
        //first pick next harvestable which not existing, when all available harvestable already exist then find from anything
        val remainingHarvestable =
            allHarvestable.filter { it !in existing }
        val availableHarvestable =
            remainingHarvestable
                .filter(filterByBiome(biome))
                .takeIf { it.isNotEmpty() }
                ?: allHarvestable.filter(filterByBiome(biome))
        return if (availableHarvestable.isNotEmpty()) {
            availableHarvestable.random(random)
        } else {
            null
        }
    }

    private fun filterByBiome(biome: BiomeType): (Harvestable) -> Boolean = {
        val config = rules.getValue(it)
        biome in config.biomes
    }

    private fun isSuitableForMine(tile: MapTerrainData): Boolean =
//        tile.noiseData.elevation in rules.getValue(harvestable).elevation &&
//                tile.noiseData.moisture in rules.getValue(harvestable).moisture &&
//                tile.noiseData.temperature in rules.getValue(harvestable).temperature
        !occupied(tile.coords) && (tile.terrain.data.walkCost ?: tile.terrain.biome.data.walkCost) > 0f
}