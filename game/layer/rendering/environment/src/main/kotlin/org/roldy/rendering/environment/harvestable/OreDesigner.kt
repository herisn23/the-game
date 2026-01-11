package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.data.mine.harvestable.Ore.*
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Environment

fun SpriteCompositor.ore(position: Vector2, biomeType: BiomeType, harvestable: Harvestable, empty: () -> Boolean) {
    if (listOf(BiomeType.MysticalForest, BiomeType.Forest).contains(biomeType))
        texture(position, { environment[Environment.mine_1_1] }, { !empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4f, 24.9f)
        }
    if (listOf(BiomeType.MysticalForest, BiomeType.Forest).contains(biomeType))
        texture(position, { environment[Environment.mine_1_2] }, { empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.000046f, 24.89998f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { environment[Environment.mine_2_1] }, { !empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.5f, 24.9f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { environment[Environment.mine_2_2] }, { empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-3.509976f, 24.89998f)
        }
    if (listOf(BiomeType.Cold).contains(biomeType))
        texture(position, { environment[Environment.mine_3_1] }, { !empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.900002f, 26.7f)
        }
    if (listOf(BiomeType.Cold).contains(biomeType))
        texture(position, { environment[Environment.mine_3_2] }, { empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-5.050003f, 24.71992f)
        }
    if (listOf(BiomeType.Volcanic).contains(biomeType))
        texture(position, { environment[Environment.mine_4_1] }, { !empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.9f, 26.7f)
        }
    if (listOf(BiomeType.Volcanic).contains(biomeType))
        texture(position, { environment[Environment.mine_4_2] }, { empty() }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.160004f, 24.99992f)
        }
    if (listOf(BiomeType.Cold).contains(biomeType))
        texture(position, { environment[Environment.mine_snow] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-4.90007f, 28.39998f)
        }
    if (Iron == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0004_Layer_366] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-72f, -36.9f)
        }
    if (Copper == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0041_Layer_329] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-72f, -45.6f)
        }
    if (Gold == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0006_Layer_364] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-67.7f, -36.9f)
        }
    if (Silver == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0000_Layer_370] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-65.9f, -36.9f)
        }
    if (Mithril == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0020_Layer_350] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-72f, -36.9f)
        }
    if (Adamantine == harvestable)
        texture(position, { environment[Environment.mountains_0000s_0038_Layer_332] }, { true }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-67.2f, -42.1f)
        }
}