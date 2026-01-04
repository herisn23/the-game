package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.gem(position: Vector2, biomeType: BiomeType) {
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.iceBerg03] }) {
            setScale(1f, 1f)
            centered()
            offset(78.8f, 50.6f)
        }
    texture(position, { decors[Decors.clayPit01] }) {
        setScale(1f, 1f)
        centered()
        offset(6.1f, 0.8f)
    }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.desertShrub01] }) {
            setScale(1f, 1f)
            centered()
            offset(-84.6f, 78.8f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.desertTree01] }) {
            setScale(1f, 1f)
            centered()
            offset(-109.2f, 79.5f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.redRock04] }) {
            setScale(0.8f, 0.8f)
            centered()
            offset(10.3f, -40f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.desertShrub03] }) {
            setScale(0.8f, 0.8f)
            centered()
            offset(-113.1f, 44.3f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.desertTree02] }) {
            setScale(1f, 1f)
            centered()
            offset(-13.6f, 127.2f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.redRock06] }) {
            setScale(0.8f, 0.8f)
            centered()
            offset(-32.2f, 98.7f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.redDesertGrass03] }) {
            setScale(0.8f, 0.8f)
            centered()
            offset(97.8f, -56f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.desertTree04] }) {
            setScale(1f, 1f)
            centered()
            offset(62.1f, -46f)
        }
    if (listOf(BiomeType.Desert, BiomeType.DeepDesert).contains(biomeType))
        texture(position, { decors[Decors.cactus01] }) {
            setScale(1f, 1f)
            centered()
            offset(-50.3f, 113.9f)
        }
    if (listOf(BiomeType.Desert, BiomeType.DeepDesert).contains(biomeType))
        texture(position, { decors[Decors.cactus00] }) {
            setScale(1f, 1f)
            centered()
            offset(-7.9f, 136.5f)
        }
    if (listOf(BiomeType.Desert, BiomeType.DeepDesert).contains(biomeType))
        texture(position, { decors[Decors.cactus05] }) {
            setScale(1f, 1f)
            centered()
            offset(-102.8f, 5.8f)
        }
    if (listOf(BiomeType.Desert, BiomeType.DeepDesert).contains(biomeType))
        texture(position, { decors[Decors.cactus08] }) {
            setScale(1f, 1f)
            centered()
            offset(-17.2f, 106.6f)
        }
    if (listOf(BiomeType.Desert, BiomeType.DeepDesert).contains(biomeType))
        texture(position, { decors[Decors.cowSkull00] }) {
            setScale(1f, 1f)
            centered()
            offset(64.5f, -68.6f)
        }
    if (listOf(BiomeType.Desert, BiomeType.DeepDesert).contains(biomeType))
        texture(position, { decors[Decors.redRockSmall00] }) {
            setScale(1f, 1f)
            centered()
            offset(10.7f, -49.4f)
        }
    if (listOf(BiomeType.Jungle).contains(biomeType))
        texture(position, { decors[Decors.palm00] }) {
            setScale(1f, 1f)
            centered()
            offset(-114f, 14.7f)
        }
    if (listOf(BiomeType.Jungle).contains(biomeType))
        texture(position, { decors[Decors.palm01] }) {
            setScale(1f, 1f)
            centered()
            offset(-105.4f, 67.8f)
        }
    if (listOf(BiomeType.Jungle).contains(biomeType))
        texture(position, { decors[Decors.palm01] }) {
            setScale(1f, 1f)
            centered()
            offset(101.2f, -31.1f)
        }
    if (listOf(BiomeType.Jungle).contains(biomeType))
        texture(position, { decors[Decors.stiltHouse04] }) {
            setScale(1f, 1f)
            centered()
            offset(81.1f, -44.4f)
        }
    if (listOf(BiomeType.Jungle).contains(biomeType))
        texture(position, { decors[Decors.junglePalm02] }) {
            setScale(1f, 1f)
            centered()
            offset(-66.2f, 99.7f)
        }
    if (listOf(BiomeType.Jungle).contains(biomeType))
        texture(position, { decors[Decors.palm05] }) {
            setScale(1f, 1f)
            centered()
            offset(-15.1f, 111f)
        }
    if (listOf(BiomeType.Jungle).contains(biomeType))
        texture(position, { decors[Decors.palm00] }) {
            setScale(1f, 1f)
            centered()
            offset(48.7f, -56.3f)
        }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.treePineSnow01] }) {
            setScale(1f, 1f)
            centered()
            offset(104.7f, -29.2f)
        }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.treePine00] }) {
            setScale(1f, 1f)
            centered()
            offset(-45.4f, 115f)
        }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.treePine01] }) {
            setScale(1f, 1f)
            centered()
            offset(-16.8f, 118.9f)
        }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.treePineSnow01] }) {
            setScale(1f, 1f)
            centered()
            offset(104.7f, -29.2f)
        }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.treePineSnow02] }) {
            setScale(1f, 1f)
            centered()
            offset(72.8f, -42.5f)
        }
    if (listOf(BiomeType.Volcanic).contains(biomeType))
        texture(position, { decors[Decors.fumarole03] }) {
            setScale(1f, 1f)
            centered()
            offset(-60.4f, 83.5f)
        }
    if (listOf(BiomeType.Volcanic).contains(biomeType))
        texture(position, { decors[Decors.burnedTree00] }) {
            setScale(1f, 1f)
            centered()
            offset(-21.9f, 109.4f)
        }
    if (listOf(BiomeType.Volcanic).contains(biomeType))
        texture(position, { decors[Decors.burnedTree00] }) {
            setScale(1f, 1f)
            centered()
            offset(71.7f, -53.3f)
        }
    if (listOf(BiomeType.Volcanic).contains(biomeType))
        texture(position, { decors[Decors.lavaRocks02] }) {
            setScale(1f, 1f)
            centered()
            offset(105.6f, -50f)
        }
    if (listOf(BiomeType.Volcanic).contains(biomeType))
        texture(position, { decors[Decors.fumarole01] }) {
            setScale(1f, 1f)
            centered()
            offset(-110.2f, -3.5f)
        }
    if (listOf(BiomeType.Volcanic).contains(biomeType))
        texture(position, { decors[Decors.burnedTree03] }) {
            setScale(1f, 1f)
            centered()
            offset(-101f, 71.6f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { decors[Decors.swampStump00] }) {
            setScale(1f, 1f)
            centered()
            offset(-101.7f, 64.5f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { decors[Decors.swampTree05] }) {
            setScale(1f, 1f)
            centered()
            offset(-16.1f, 115.7f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { decors[Decors.swampTree01] }) {
            setScale(1f, 1f)
            centered()
            offset(-59.2f, 109.6f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { decors[Decors.swampTree03] }) {
            setScale(1f, 1f)
            centered()
            offset(70.3f, -44.4f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { decors[Decors.swampStump01] }) {
            setScale(1f, 1f)
            centered()
            offset(102.8f, -49.7f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { decors[Decors.swampStump04] }) {
            setScale(1f, 1f)
            centered()
            offset(53f, -79.6f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treeA02] }) {
            setScale(1f, 1f)
            centered()
            offset(-105.4f, 8.4f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treeA00] }) {
            setScale(1f, 1f)
            centered()
            offset(-7.2f, 112f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.barrels00] }) {
            setScale(1f, 1f)
            centered()
            offset(-58.2f, 84.1f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.barrels01] }) {
            setScale(1f, 1f)
            centered()
            offset(70.6f, -68.6f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treeA01] }) {
            setScale(1f, 1f)
            centered()
            offset(98.5f, -44.1f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.grass01] }) {
            setScale(1f, 1f)
            centered()
            offset(-96.1f, 63.5f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treeA03] }) {
            setScale(1f, 1f)
            centered()
            offset(-105.4f, 60.8f)
        }
}