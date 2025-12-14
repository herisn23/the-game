package org.roldy.gp.world

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.modules.SerializersModule
import org.roldy.core.asset.loadAsset
import org.roldy.data.configuration.biome.BiomesConfiguration
import org.roldy.data.configuration.harvestable.HarvestableConfiguration
import org.roldy.gp.world.decode

val yaml = Yaml(
    serializersModule = SerializersModule {

    }
)

private fun readConfigurationFile(file: String) =
    loadAsset(file).readString()

inline fun <reified T> String.decode() =
    Yaml.default.decodeFromString<T>(this)

fun loadBiomesConfiguration(): BiomesConfiguration {
    val decode: BiomesConfiguration = readConfigurationFile("biomes-configuration.yaml").decode<BiomesConfiguration>()
    return decode
}

fun loadHarvestableConfiguration(): HarvestableConfiguration =
    readConfigurationFile("harvestable-configuration.yaml").decode()