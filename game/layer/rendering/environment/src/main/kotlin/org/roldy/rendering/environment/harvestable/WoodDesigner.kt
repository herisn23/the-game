package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.map.Terrain
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.rendering.environment.composite.CompositeAnimation
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Environment

fun SpriteCompositor.wood(position: Vector2, terrain: Terrain, harvestable: Harvestable, empty: () -> Boolean) {
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_3_13] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-72.6f, 105.1f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(position, { environment[Environment.tree_3_11] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-68.89995f, 50f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_4_13] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-72.10002f, 105.0999f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(position, { environment[Environment.tree_4_11] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-68.89995f, 50f)
        }
    if (listOf(BiomeType.Forest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.tree_1_30] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-72.10002f, 105.0999f)
        }
    if (listOf(BiomeType.Forest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_1_0] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-68.89995f, 50f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.tree_8_26] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-72.10002f, 105.0999f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_8_34] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-68.89995f, 50f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_7_28] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-72.10002f, 105.0999f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_6_12] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-72.10002f, 105.0999f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_5_10] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-63.69995f, 103.2f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_5_15] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-77.29995f, 105.8f)
        }
    if (listOf(BiomeType.MysticalForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_8_34] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-68.89995f, 50f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.tree_9_20] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-68.79995f, 83.7f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_8_34] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-72.79995f, 48.7f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_3_21] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(70f, 61f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(position, { environment[Environment.tree_3_11] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(71.8f, 28.59998f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_4_34] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(71.2f, 55.8f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(position, { environment[Environment.tree_4_11] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(71.8f, 28.59998f)
        }
    if (listOf(BiomeType.Forest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.tree_1_9] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(68.59993f, 83.69992f)
        }
    if (listOf(BiomeType.Forest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_1_0] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(71.8f, 28.59998f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.tree_8_24] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(63.4f, 64.9f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_8_34] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(71.8f, 28.59998f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_7_12] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(64.7f, 81.2f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_6_5] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(64.1f, 70.7f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_5_11] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(66f, 73.4f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_5_6] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(75.1f, 85f)
        }
    if (listOf(BiomeType.MysticalForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_8_34] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(71.8f, 28.59998f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.tree_10_29] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(58.9f, 71.4f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_8_34] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(67.9f, 27.29997f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.water_0001s_0035_Layer_291] }, { true }) {
            setScale(0.5f, 0.5f)
            centered()
            offset(12.10001f, 5.899994f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.water_0001s_0005_Layer_321] }, { true }) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-1.5f, 2.699997f)
        }
    if (listOf(BiomeType.Forest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.water_0001s_0023_Layer_303] }, { true }) {
            setScale(0.5f, 0.5f)
            centered()
            offset(1.699997f, 4.600006f)
        }
    if (listOf(BiomeType.Cold).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.water_0001s_0008_Layer_318] }, { true }) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-6.7f, 11.7f)
        }
    if (listOf(BiomeType.MysticalForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.water_0001s_0020_Layer_306] }, { true }) {
            setScale(0.5f, 0.5f)
            centered()
            offset(-8f, 11.7f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_3_31] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-47.3f, -22.6f)
        }
    if ("blue_cold" == terrain.data.groupKey)
        texture(position, { environment[Environment.tree_3_11] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-44.2f, -52.40002f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_4_26] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-43.5f, -21.3f)
        }
    if ("green_cold" == terrain.data.groupKey)
        texture(position, { environment[Environment.tree_4_11] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-44.2f, -52.40002f)
        }
    if (listOf(BiomeType.Forest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.tree_1_19] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-46.8f, -7f)
        }
    if (listOf(BiomeType.Forest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_1_0] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-44.2f, -52.40002f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.tree_8_30] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-49.4f, -2.5f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_8_34] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-44.2f, -52.40002f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_7_5] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-51.3f, 0.1999969f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_6_19] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-51.9f, -10.3f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_5_8] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-46.1f, -10.8f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(position, { environment[Environment.tree_5_11] }, { !empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-48.7f, -6.4f)
        }
    if (listOf(BiomeType.MysticalForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_8_34] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-44.2f, -52.40002f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.tree_9_30] },
            { !empty() },
            CompositeAnimation(Scale, 0.04f, 0.1f, 5f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-46.1f, -23.9f)
        }
    if (listOf(BiomeType.WitchForest).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.tree_8_34] }, { empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-48.1f, -53.70003f)
        }
}