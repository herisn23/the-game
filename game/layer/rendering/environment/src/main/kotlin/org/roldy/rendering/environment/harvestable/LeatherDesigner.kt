package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.map.Terrain
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.rendering.environment.composite.CompositeAnimation
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Environment

fun SpriteCompositor.leather(position: Vector2, terrain: Terrain, harvestable: Harvestable, empty: () -> Boolean) {
    texture(position, { environment[Environment.beast_den_1_1] }, { !empty() }) {
        setScale(0.3f, 0.3f)
        centered()
        offset(3.899994f, 31.89999f)
    }
    texture(position, { environment[Environment.beast_den_1_1_closed] }, { empty() }) {
        setScale(0.3f, 0.3f)
        centered()
        offset(3.899994f, 31.89999f)
    }
    if (listOf(BiomeType.Cold).contains(terrain.biome.data.type))
        texture(position, { environment[Environment.beast_den_snow] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(1.699997f, 42f)
        }
    if (listOf(BiomeType.Forest).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.forest_0000s_0000_Layer_285] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(98.4f, 82.1f)
        }
    if (listOf(BiomeType.Cold).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.forest_0000s_0014_Layer_271] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-82.4f, 42f)
        }
    if ("red_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.forest_0000s_0055_Layer_230] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(94.6f, 22.2f)
        }
    if ("blue_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.forest_0000s_0011_Layer_274] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(72.3f, 78.3f)
        }
    if ("dark_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.tree_5_12] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(74.3f, 23.1f)
        }
    if ("bright_green_mystical" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.forest_0000s_0000_Layer_285] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(81.1f, 90.9f)
        }
    if (listOf(BiomeType.Swamp).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.forest_0000s_0062_Layer_223] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(90.8f, 36.3f)
        }
    if (listOf(BiomeType.Volcanic).contains(terrain.biome.data.type))
        texture(
            position,
            { environment[Environment.forest_0000s_0040_Layer_245] },
            { true },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(1f, 1f)
            centered()
            offset(-65.4f, 61.4f)
        }
}