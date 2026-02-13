package org.roldy.core.configuration

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import org.roldy.core.asset.loadAsset
import org.roldy.core.biome.BiomeType
import org.roldy.core.configuration.data.BiomesConfiguration
import org.roldy.core.configuration.data.FoliageMaterialConfiguration
import org.roldy.core.configuration.data.ModelInstancesConfiguration


val yaml = Yaml(
    configuration = YamlConfiguration(
        allowAnchorsAndAliases = true
    )
)

private fun readConfigurationFile(file: String) =
    loadAsset(file).readString()

inline fun <reified T> String.decode() =
    yaml.decodeFromString<T>(this)

fun loadBiomesConfiguration(): BiomesConfiguration =
    readConfigurationFile("biomes-configuration.yaml").decode()

fun loadFoliageMaterialConfiguration(biome: BiomeType): FoliageMaterialConfiguration =
    readConfigurationFile("material-${biome.name.lowercase()}.yaml").decode()

fun loadModelInstanceConfiguration(biome: BiomeType): ModelInstancesConfiguration =
    readConfigurationFile("model-${biome.name.lowercase()}.yaml").decode()
