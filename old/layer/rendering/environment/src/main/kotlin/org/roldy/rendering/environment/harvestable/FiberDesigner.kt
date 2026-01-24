package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.map.Terrain
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.rendering.environment.composite.CompositeAnimation
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Environment

fun SpriteCompositor.fiber(position: Vector2, terrain: Terrain, harvestable: Harvestable, empty: () -> Boolean) {
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0004_Layer_88] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(5.2f, 72.7f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0193_Layer_26] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(33.8f, 80.4f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0005_Layer_87] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-56.4f, 4.600006f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0003_Layer_89] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(11.7f, -46f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0002_Layer_90] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(5.2f, 72.7f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0189_Layer_30] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(20.8f, 82.4f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0001_Layer_91] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-56.4f, 4.600006f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0000_Layer_92] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(11.7f, -46f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0016_Layer_76] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-36.3f, 67.5f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0214_Layer_5] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(1.399994f, 102.5f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0018_Layer_74] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(64.9f, -10.3f)
        }
    if ("dry_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0017_Layer_75] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-32.4f, -42.1f)
        }
    if ("wet_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0009_Layer_83] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-36.3f, 67.5f)
        }
    if ("wet_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0197_Layer_22] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(4.600006f, 112.9f)
        }
    if ("wet_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0015_Layer_77] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(64.9f, -10.3f)
        }
    if ("wet_forest" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0014_Layer_78] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-32.4f, -42.1f)
        }
    if ("dark_green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0129_Layer_90] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-36.3f, 67.5f)
        }
    if ("dark_green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0200_Layer_19] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(4f, 98.6f)
        }
    if ("dark_green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0131_Layer_88] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(64.9f, -10.3f)
        }
    if ("dark_green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0133_Layer_86] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-32.4f, -42.1f)
        }
    if ("green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0114_Layer_105] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-36.3f, 67.5f)
        }
    if ("green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0064_Layer_9] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(5.3f, 54.5f)
        }
    if ("green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0116_Layer_103] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(64.9f, -10.3f)
        }
    if ("green_swamp" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0118_Layer_101] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-32.4f, -42.1f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0063_Layer_29] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(38.9f, -46.6f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0065_Layer_27] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(24.1f, 41.6f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0209_Layer_10] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(6.600006f, 115.4f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0066_Layer_26] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-52.5f, -5.8f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0142_Layer_77] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-59f, 17.6f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0146_Layer_73] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(37.7f, 59.8f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0071_Layer_2] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(0.8000031f, 68.10001f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0144_Layer_75] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.6f, 0.6f)
            centered()
            offset(47.3f, -7.1f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0059_Layer_33] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(38.9f, -46.6f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0061_Layer_31] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(24.1f, 41.6f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0205_Layer_14] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(6.600006f, 115.4f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0062_Layer_30] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-52.5f, -5.8f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0059_Layer_33] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(38.9f, -46.6f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0061_Layer_31] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(24.1f, 41.6f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0205_Layer_14] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(6.600006f, 115.4f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0062_Layer_30] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-52.5f, -5.8f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.grass_0000_Layer_22] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-64.8f, 31.2f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.grass_0000s_0031_Layer_61] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(33.1f, -53.7f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.grass_0000s_0032_Layer_60] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(29.9f, 56.5f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.grass_0205_Layer_14] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.035f, 3.5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(10.5f, 93.4f)
        }
}