package org.roldy.gameplay.world.generator

import org.roldy.core.Vector2Int
import org.roldy.core.logger
import org.roldy.core.utils.hexDistance
import org.roldy.core.x
import org.roldy.data.configuration.harvestable.HarvestableConfiguration
import org.roldy.data.map.MapData
import org.roldy.data.tile.mine.MineData
import org.roldy.data.tile.mine.MineType
import org.roldy.data.tile.mine.harvestable.Harvestable
import org.roldy.data.tile.settlement.SettlementData
import org.roldy.rendering.map.MapTerrainData
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MineGenerator(
    val terrainData: Map<Vector2Int, MapTerrainData>,
    val mapData: MapData,
    val settlements: List<SettlementData>,
    harvestableConfiguration: HarvestableConfiguration
) {
    val rules = harvestableConfiguration.rules
    val logger by logger()

    fun generate(): List<MineData> {
        val seed = mapData.seed
        val mineRng = Random(seed + 2)
        val mines = mutableListOf<MineData>()
        val allHarvestables = MineType.harvestable

        // Step 1: Generate one mine of each harvestable type for each settlement
        for (settlement in settlements) {
            logger.debug { "Generating mines for settlement ${settlement.name} (region size: ${settlement.radius.size})" }

            for (harvestable in settlement.harvestable) {
                val mine = generateMineInSettlementRegion(
                    settlement = settlement,
                    harvestable = harvestable,
                    existingMines = mines,
                    rng = mineRng
                )

                if (mine != null) {
                    mines.add(mine)
                    logger.debug { "Generated guaranteed $harvestable for ${settlement.name} at ${mine.coords}" }
                } else {
                    logger.error { "Failed to generate $harvestable mine for ${settlement.name}" }
                }
            }
        }

        // Step 2: Generate additional random mines within settlement regions
        val additionalMinesCount = settlements.size * 2 // 2 extra mines per settlement
        val attempts = additionalMinesCount * 10

        repeat(attempts) {
            if (mines.size >= settlements.sumOf { it.harvestable.size } + additionalMinesCount) {
                return@repeat
            }

            // Pick random settlement and generate mine in its region
            val settlement = settlements.randomOrNull(mineRng) ?: return@repeat
            val harvestable = allHarvestables.random(mineRng)

            val mine = generateMineInSettlementRegion(
                settlement = settlement,
                harvestable = harvestable,
                existingMines = mines,
                rng = mineRng
            )

            if (mine != null) {
                mines.add(mine)
                logger.debug { "Generated additional $harvestable in ${settlement.name} region at ${mine.coords}" }
            }
        }

        logger.info {
            "Generated ${mines.size} mines total (${
                mines.groupBy { it.harvestable }.mapValues { it.value.size }
            })"
        }
        return mines
    }

    private fun generateMineInSettlementRegion(
        settlement: SettlementData,
        harvestable: Harvestable,
        existingMines: List<MineData>,
        rng: Random
    ): MineData? {
        val maxAttempts = 50

        repeat(maxAttempts) {
            // Generate position within the settlement's region radius
            val angle = rng.nextDouble() * 2 * Math.PI
            val minDistance = 2 // Minimum distance from settlement center
            val maxDistance = settlement.radius.size
            val distance = rng.nextInt(minDistance, maxDistance + 1)

            val x = (settlement.coords.x + distance * cos(angle)).toInt()
            val y = (settlement.coords.y + distance * sin(angle)).toInt()
            val coords = x x y

            // Check if coordinates are within map bounds
            if (x !in 0 until mapData.size.width || y !in 0 until mapData.size.height) {
                return@repeat
            }

            val tile = terrainData[coords] ?: return@repeat

            // Check if location is suitable for mining
            val isSuitable = isSuitableForMine(tile, harvestable) &&
                    existingMines.none {
                        hexDistance(x x y, it.coords.x x it.coords.y) < 3 // Min distance from other mines
                    } &&
                    hexDistance(
                        x x y,
                        settlement.coords.x x settlement.coords.y
                    ) >= 2 // Not too close to settlement center

            if (isSuitable) {
                return MineData(
                    coords = coords,
                    name = "$harvestable ${existingMines.count { it.harvestable == harvestable } + 1}",
                    harvestable = harvestable
                )
            }
        }

        return null
    }

    private fun isSuitableForMine(tile: MapTerrainData, harvestable: Harvestable): Boolean =
        tile.noiseData.elevation in rules.getValue(harvestable).elevation &&
                tile.noiseData.moisture in rules.getValue(harvestable).moisture &&
                tile.noiseData.temperature in rules.getValue(harvestable).temperature
}