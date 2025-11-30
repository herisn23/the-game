package org.roldy.terrain.biome

context(noiseData: BiomeMapGenerator.NoiseData)
fun findTerrain(biomes: List<Biome>): Terrain? =
    biomes.find()?.findTerrain()

context(noiseData: BiomeMapGenerator.NoiseData)
internal fun List<Biome>.find(): Biome? =
    find {
        noiseData inRange it.data
    }

context(noiseData: BiomeMapGenerator.NoiseData)
internal fun Biome.findTerrain(): Terrain? =
    terrains.find {
        noiseData inRange it.data
    }

//fun configure(): List<Biome> =
//    listOf(
//        biome("Grass") {
//            emptyList()
//        }
//    )
//
//
//fun getBiome(elevation: Float, temperature: Float, moisture: Float): Biome {
//    // Water biomes
//    if (elevation < -0.2f) {
//        return Biome.OCEAN
//    }
//
//    // Beach/coast
//    if (elevation < 0.0f) {
//        return Biome.BEACH
//    }
//
//    // Mountains
//    if (elevation > 0.7f) {
//        return if (temperature < 0.3f) Biome.SNOW else Biome.MOUNTAIN
//    }
//
//    // Cold biomes
//    if (temperature < 0.2f) {
//        return if (moisture < 0.3f) Biome.TUNDRA else Biome.SNOW
//    }
//
//    // Cool biomes
//    if (temperature < 0.4f) {
//        return when {
//            moisture < 0.3f -> Biome.GRASSLAND
//            moisture < 0.6f -> Biome.TEMPERATE_FOREST
//            else -> Biome.TAIGA
//        }
//    }
//
//    // Warm biomes
//    if (temperature < 0.7f) {
//        return when {
//            moisture < 0.3f -> Biome.SAVANNA
//            moisture < 0.6f -> Biome.GRASSLAND
//            else -> Biome.TEMPERATE_FOREST
//        }
//    }
//
//    // Hot biomes
//    return when {
//        moisture < 0.3f -> Biome.DESERT
//        moisture < 0.6f -> Biome.SAVANNA
//        else -> Biome.TROPICAL_FOREST
//    }
//}