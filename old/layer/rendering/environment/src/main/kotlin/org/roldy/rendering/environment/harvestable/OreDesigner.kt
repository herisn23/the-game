package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.map.Terrain
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.data.mine.harvestable.Ore.*
import org.roldy.rendering.environment.composite.CompositeAnimation
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Environment

fun SpriteCompositor.ore(position: Vector2, terrain: Terrain, harvestable: Harvestable, empty: () -> Boolean) {
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000_Layer_73] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-3.9f, 116.5f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000_Layer_73] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-37.8f, 96f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000_Layer_219] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-31.1f, 100.5f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0000_Layer_92] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-83.7f, 68.8f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0001_Layer_91] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(19.7f, 100.9f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0002_Layer_90] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(71f, 76.4f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0003_Layer_89] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-37.8f, 96f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0004_Layer_88] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(14.4f, 102.7f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0005_Layer_87] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-81f, 74.6f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000_Layer_219] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(73.8f, 77.7f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0003_Layer_89] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-86.9f, 43.4f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0025_Layer_48] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-1.199997f, 114.2f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0039_Layer_53] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-74.4f, 67f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0039_Layer_53] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-28.5f, 93.3f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0039_Layer_53] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(77.7f, 63.4f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0054_Layer_165] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(36.7f, 102.2f)
        }
    if ("wet_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0010_Layer_82] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-3.5f, 100.9f)
        }
    if ("wet_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0012_Layer_80] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-64.6f, 70.5f)
        }
    if ("wet_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0014_Layer_78] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(58.5f, 67.8f)
        }
    if ("dark_green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0071_Layer_20] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-69.5f, 64.7f)
        }
    if ("dark_green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0073_Layer_18] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-13.8f, 91f)
        }
    if ("dark_green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0074_Layer_17] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(61.2f, 71.4f)
        }
    if ("green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0075_Layer_16] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-69.5f, 69.6f)
        }
    if ("green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0078_Layer_13] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-10.6f, 104.8f)
        }
    if ("green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0077_Layer_14] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(64.3f, 72.2f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0065_Layer_27] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-14.6f, 98.5f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0064_Layer_28] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-77f, 74.4f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0066_Layer_26] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(43.9f, 84.3f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0064_Layer_28] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(84.1f, 57.1f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0061_Layer_31] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-11.8f, 96.3f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0059_Layer_33] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-76.5f, 68.6f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0062_Layer_30] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(66.2f, 74.8f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0035_Layer_57] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-0.6999969f, 96.2f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0047_Layer_45] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-69f, 72.3f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0047_Layer_45] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(70.3f, 70.7f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0044_Layer_175] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-74.9f, 71.2f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0045_Layer_174] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(1.899994f, 116.2f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0047_Layer_172] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.6f, 0.6f)
            centered()
            offset(77.6f, 78.1f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.grass_0203_Layer_16] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.6f, 0.6f)
            centered()
            offset(-54.3f, 114.1f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.grass_0204_Layer_15] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.6f, 0.6f)
            centered()
            offset(-13.5f, 134.2f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.grass_0206_Layer_13] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.6f, 0.6f)
            centered()
            offset(60.1f, 123.1f)
        }
    if (listOf(BiomeType.MysticalForest, BiomeType.Forest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.mine_1_1] }, { !empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4f, 24.9f)
        }
    if (listOf(BiomeType.MysticalForest, BiomeType.Forest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.mine_1_2] }, { empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.000046f, 24.89998f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.mine_2_1] }, { !empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.5f, 24.9f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.mine_2_2] }, { empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-3.509979f, 24.89998f)
        }
    if (listOf(BiomeType.Cold).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.mine_3_1] }, { !empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.900002f, 26.7f)
        }
    if (listOf(BiomeType.Cold).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.mine_3_2] }, { empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-5.050003f, 24.71992f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.mine_4_1] }, { !empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.9f, 26.7f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.mine_4_2] }, { empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.160004f, 24.99992f)
        }
    if (listOf(BiomeType.Cold).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.mine_snow] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.90007f, 28.39998f)
        }
    if (Iron == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0004_Layer_366] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-72f, -20.9f)
        }
    if (Copper == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0041_Layer_329] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-72f, -29.6f)
        }
    if (Gold == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0006_Layer_364] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-67.7f, -20.9f)
        }
    if (Silver == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0000_Layer_370] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-65.9f, -20.9f)
        }
    if (Mithril == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0020_Layer_350] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-72f, -20.9f)
        }
    if (Adamantine == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0038_Layer_332] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-67.2f, -26.1f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0005_Layer_87] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(84.9f, -32f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0000_Layer_92] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(83.5f, -36.9f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0039_Layer_53] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(41.9f, -66.8f)
        }
    if ("dark_green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0072_Layer_19] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(71.9f, -50.4f)
        }
    if ("green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0076_Layer_15] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(74.1f, -47.3f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0051_Layer_41] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(21.6f, -70.5f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0046_Layer_46] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(32.3f, -74.6f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0062_Layer_30] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(35.4f, -65.2f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0046_Layer_173] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.6f, 0.6f)
            centered()
            offset(37.3f, -65.5f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.grass_0205_Layer_14] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.6f, 0.6f)
            centered()
            offset(71.8f, -5.1f)
        }
    if ("wet_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0009_Layer_83] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.3f, 0.3f)
            centered()
            offset(82.1f, -45.9f)
        }
}