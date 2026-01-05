package org.roldy.gp.world.generator

import org.roldy.data.configuration.biome.BiomeData
import org.roldy.data.configuration.match
import org.roldy.rendering.map.MapTerrainData


fun BiomeData.SpawnData.match(mapData: MapTerrainData): Boolean =
    if (spawnAt != null || spawnAtGroup != null) {
        matchByKey(mapData)
    } else {
        match(mapData.noiseData)
    }


fun BiomeData.SpawnData.matchByKey(mapData: MapTerrainData): Boolean {
    val key = mapData.terrain.data.key
    val groupKey = mapData.terrain.data.groupKey
    return (spawnAt?.let { it == key } == true || spawnAtGroup?.let { it == groupKey } == true)
}