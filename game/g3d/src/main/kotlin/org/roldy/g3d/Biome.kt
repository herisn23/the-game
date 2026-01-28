package org.roldy.g3d

enum class Biome(val index: Int, val textureName: String) {
    WATER(0, "Water_6_Albedo"),
    BEACH(1, "Bones_Debris_1_Albedo"),
    DESERT(2, "Rippled_Sand_2_Albedo"),
    SAVANNA(3, "Fur_3_Albedo"),
    GRASSLAND(4, "Grass_37_Albedo"),
    FOREST(5, "Planks_Wood_16_Albedo"),
    RAINFOREST(6, "Forest_Ground_38_Albedo"),
    TUNDRA(7, "Crystal_Surface_1_Albedo"),
    SNOW(8, "Dirty_Snow_2_Albedo"),
    MOUNTAIN(9, "Cliff_Rock_Surface_21_Albedo"),
    SWAMP(10, "Muddy_Cracked_Sand_6_Albedo"),
    DIRT(11, "Wood_Wall_1_Albedo");

    companion object {
        fun fromClimate(elevation: Float, temperature: Float, moisture: Float, slope: Float): Biome {
            // Steep slopes = rock
            if (slope > 0.5f) return MOUNTAIN

            // Water
            if (elevation < 0.15f) return WATER

            // Beach
            if (elevation < 0.2f) return BEACH

            // Snow - high elevation or very cold
            if (elevation > 0.85f) return SNOW
            if (temperature < 0.15f) return SNOW

            // Mountain/Rock
            if (elevation > 0.75f) return MOUNTAIN

            // Tundra - cold
            if (temperature < 0.25f) return TUNDRA

            // Desert - hot and dry
            if (temperature > 0.7f && moisture < 0.25f) return DESERT

            // Savanna - hot, moderate moisture
            if (temperature > 0.6f && moisture < 0.4f) return SAVANNA

            // Swamp - wet
            if (moisture > 0.8f && elevation < 0.4f) return SWAMP

            // Rainforest - hot and wet
            if (temperature > 0.6f && moisture > 0.6f) return RAINFOREST

            // Forest - moderate temp, wet
            if (moisture > 0.5f) return FOREST

            // Grassland
            return GRASSLAND
        }
    }
}