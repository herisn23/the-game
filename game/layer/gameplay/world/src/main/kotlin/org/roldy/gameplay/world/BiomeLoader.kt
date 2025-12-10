package org.roldy.gameplay.world

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import org.roldy.core.asset.loadAsset
import org.roldy.data.biome.BiomeData
import org.roldy.data.biome.BiomesConfiguration


fun loadBiomesConfiguration(biomesConfiguration: String): List<BiomeData> =
    loadAsset(biomesConfiguration).readString()
        .run {
            Yaml.default.decodeFromString<BiomesConfiguration>(this).biomes
        }