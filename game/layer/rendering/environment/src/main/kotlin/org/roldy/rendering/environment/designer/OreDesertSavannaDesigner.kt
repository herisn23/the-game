package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors
import org.roldy.rendering.tiles.Tiles

fun SpriteCompositor.oredesertsavanna(position: Vector2, biomeType: BiomeType) {
    if (listOf(BiomeType.Desert, BiomeType.DeepDesert).contains(biomeType))
        texture(position, { tiles[Tiles.hexDesertYellowBase03] })
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { tiles[Tiles.hexDesertRedBase00] })
    texture(position, { decors[Decors.redRock00] }) {
        setScale(1f, 1f)
        centered()
        offset(2.6f, 111.3f)
    }
    texture(position, { decors[Decors.redRockBig02] }) {
        setScale(1f, 1f)
        centered()
        offset(59f, 60.10002f)
    }
    texture(position, { decors[Decors.mines01] }) {
        setScale(1f, 1f)
        centered()
        offset(80.59995f, 51.50002f)
    }
    texture(position, { decors[Decors.mines04] }) {
        setScale(0.7f, 0.7f)
        centered()
        offset(-49.1f, 79.3f)
    }
    texture(position, { decors[Decors.redRockBig01] }) {
        setScale(1f, 1f)
        centered()
        offset(17.89995f, 50.80003f)
    }
    texture(position, { decors[Decors.redRockBig00] }) {
        setScale(1f, 1f)
        centered()
        offset(-37.20005f, 9.000023f)
    }
    if (listOf(BiomeType.DeepDesert, BiomeType.Desert).contains(biomeType))
        texture(position, { decors[Decors.cactus01] }) {
            setScale(1f, 1f)
            centered()
            offset(-0.7f, -68f)
        }
    if (listOf(BiomeType.DeepDesert, BiomeType.Desert).contains(biomeType))
        texture(position, { decors[Decors.cactus00] }) {
            setScale(1f, 1f)
            centered()
            offset(37.8f, 9f)
        }
    if (listOf(BiomeType.DeepDesert, BiomeType.Desert).contains(biomeType))
        texture(position, { decors[Decors.cactus05] }) {
            setScale(1f, 1f)
            centered()
            offset(69.6f, -35.5f)
        }
    if (listOf(BiomeType.DeepDesert, BiomeType.Desert).contains(biomeType))
        texture(position, { decors[Decors.cowSkull00] }) {
            setScale(1f, 1f)
            centered()
            offset(88.8f, -2.3f)
        }
    if (listOf(BiomeType.Desert).contains(biomeType))
        texture(position, { decors[Decors.redRockSmall00] }) {
            setScale(1f, 1f)
            centered()
            offset(30.4f, -42.1f)
        }
    if (listOf(BiomeType.DeepDesert).contains(biomeType))
        texture(position, { decors[Decors.redRockSmall02] }) {
            setScale(1f, 1f)
            centered()
            offset(37.7f, -60.7f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.desertShrub01] }) {
            setScale(0.7f, 0.7f)
            centered()
            offset(86.2f, -32.1f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.redRock04] }) {
            setScale(0.7f, 0.7f)
            centered()
            offset(37.1f, -32.8f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.desertTree01] }) {
            setScale(1f, 1f)
            centered()
            offset(12.4f, -63.4f)
        }
    if (listOf(BiomeType.Savanna).contains(biomeType))
        texture(position, { decors[Decors.desertTree00] }) {
            setScale(1f, 1f)
            centered()
            offset(74.9f, -4.3f)
        }
}