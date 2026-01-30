package org.roldy.core.biome

/**
 * Enum for biomes.
 * Indexes are positions of textures in tileset
 */
enum class BiomeType {
    Arctic,
    Jungle,
    Desert,
    Savanna,
    Meadow,
    Swamp,
    Volcanic,
    Water,
    Beach
}

data class TextureMapping(
    val biome: BiomeType,
    val mountainIndexes: List<Int>,
    val groundIndexes: List<Int>
)

object BiomeTextureMapping {
    val mapping: List<TextureMapping> = listOf(
        TextureMapping(BiomeType.Arctic, listOf(13, 14, 15), listOf(0, 1, 2)),
        TextureMapping(BiomeType.Jungle, emptyList(), listOf(7, 8, 9)),
        TextureMapping(BiomeType.Desert, emptyList(), listOf(4, 5, 6)),
        TextureMapping(BiomeType.Savanna, emptyList(), listOf(23, 24, 25)),
        TextureMapping(BiomeType.Meadow, listOf(16, 17, 18), listOf(10, 11, 12)),
        TextureMapping(BiomeType.Swamp, emptyList(), listOf(26, 27, 28)),
        TextureMapping(BiomeType.Volcanic, listOf(19, 20, 21), listOf(30, 31)),
        TextureMapping(BiomeType.Water, emptyList(), listOf(29)),
        TextureMapping(BiomeType.Beach, emptyList(), listOf(3, 22))
    )
}