package org.roldy.gameplay.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.utils.repeat
import org.roldy.core.x
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.configuration.harvestable.HarvestableConfiguration
import org.roldy.data.map.MapData
import org.roldy.data.tile.mine.MineData
import org.roldy.data.tile.mine.MineType
import org.roldy.data.tile.mine.harvestable.Harvestable
import org.roldy.data.tile.settlement.SettlementData
import org.roldy.rendering.map.MapTerrainData
import kotlin.random.Random

class MineGenerator(
    val terrainData: Map<Vector2Int, MapTerrainData>,
    val mapData: MapData,
    val settlements: List<SettlementData>,
    harvestableConfiguration: HarvestableConfiguration
) {
    val rules = harvestableConfiguration.rules
    val logger by logger()
    val allHarvestable by lazy { MineType.harvestable }

    fun generate(): List<MineData> =
        generateMinesInSettlements() + generateMinesOutsideSettlements()

    private fun generateMinesInSettlements(): MutableList<MineData> {
        val mines = mutableListOf<MineData>()
        settlements.forEach { settlement ->
            val radius = settlement.radius.toMutableSet()
            val settlementMines = mutableListOf<MineData>()
            repeat(settlement.harvestableCount) { i ->
                val random = Random(mapData.seed + settlement.coords.sum + i)
                val minePosition = radius.random(random).also(radius::remove)
                val harvestable =
                    pickHarvestableForBiome(random, minePosition, settlementMines.map(MineData::harvestable))
                MineData(
                    coords = minePosition,
                    harvestable = harvestable,
                    settlementData = settlement,
                ).let(settlementMines::add)
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

    private fun generateMinesOutsideSettlements(): MutableList<MineData> {
        val mines = mutableListOf<MineData>()
        val claimedCoords = mutableSetOf<Vector2Int>()
        val maxMines = mapData.size.size
        val attempts = maxMines * 10
        val regionsHexes = settlements.flatMap { it.radius }
        val rngX = Random(mapData.seed + GeneratorSeeds.MINES_SEED + 1)
        val rngY = Random(mapData.seed + GeneratorSeeds.MINES_SEED + 2)

        repeat(attempts) {
            if (mines.size >= maxMines) return@repeat

            val coords = tryFindCoords(rngX, rngY)
            val biome = terrainData.getValue(coords).terrain.biome.data.type

            val coordsRng = Random(mapData.seed + GeneratorSeeds.MINES_SEED + coords.sum)
            val terrainData = terrainData.getValue(coords)

            if (coords !in regionsHexes && coords !in claimedCoords) {
                val harvestable = allHarvestable.filter(filterByBiome(biome)).random(coordsRng)
                if (isSuitableForMine(terrainData, harvestable)) {
                    claimedCoords.add(coords)
                    mines.add(
                        MineData(
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

    fun tryFindCoords(randomX: Random, randomY:Random): Vector2Int {
        val x = randomX.nextInt(mapData.size.width)
        val y = randomY.nextInt(mapData.size.height)
        val coords = x x y
        val biome = terrainData.getValue(coords).terrain.biome.data.type
        if (biome == BiomeType.Water) {
            return tryFindCoords(randomX, randomY)
        }
        return coords
    }


    private fun pickHarvestableForBiome(random: Random, coords: Vector2Int, existing: List<Harvestable>): Harvestable {
        val biome = terrainData.getValue(coords).terrain.biome.data.type
        //first pick next harvestable which not existing, when all available harvestable already exist then find from anything
        val remainingHarvestable =
            allHarvestable.filter { it !in existing }
        val availableHarvestable =
            remainingHarvestable
                .filter(filterByBiome(biome))
                .takeIf { it.isNotEmpty() }
                ?: allHarvestable.filter(filterByBiome(biome))
        return availableHarvestable.random(random)
    }

    private fun filterByBiome(biome: BiomeType): (Harvestable) -> Boolean = {
        val config = rules.getValue(it)
        biome in config.biomes
    }

    private fun isSuitableForMine(tile: MapTerrainData, harvestable: Harvestable): Boolean =
//        tile.noiseData.elevation in rules.getValue(harvestable).elevation &&
//                tile.noiseData.moisture in rules.getValue(harvestable).moisture &&
//                tile.noiseData.temperature in rules.getValue(harvestable).temperature
        true
}