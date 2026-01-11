package org.roldy.gameplay.scene

import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.state.GameState
import org.roldy.data.state.HeroState
import org.roldy.rendering.map.WorldMap

fun HeroState.setSuitableSpot(state: GameState, map: WorldMap) {
    fun findSettlementInBiome(biome: BiomeType) =
        state.settlements.find {
            val data = map.terrainData.getValue(it.coords)
            it.type == 0//&& data.terrain.biome.data.type == biome &&
        }?.coords

    val terrainFinder = TerrainFinder(map)

    val forestCoords = findSettlementInBiome(BiomeType.Forest) ?: terrainFinder.findCenterMostTerrain(BiomeType.Forest)

    coords = forestCoords.copy()
}
