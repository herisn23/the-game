package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.orecoldforestswamp(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.decorMountain02] }) {
        setScale(1f, 1f)
        centered()
        offset(-4.6f, 78.6f)
    }
    texture(position, { decors[Decors.mines02] }) {
        setScale(1f, 1f)
        centered()
        offset(-68f, 74f)
    }
    texture(position, { decors[Decors.decorMountain05] }) {
        setScale(1f, 1f)
        centered()
        offset(32.4f, 30.9f)
    }
    texture(position, { decors[Decors.mines01] }) {
        setScale(1f, 1f)
        centered()
        offset(74.6f, 34.2f)
    }
    texture(position, { decors[Decors.decorMountain00] }) {
        setScale(1f, 1f)
        centered()
        offset(-12.7f, -9f)
    }
    texture(position, { decors[Decors.outcrop01] }) {
        setScale(1f, 1f)
        centered()
        offset(-14.3f, -21.6f)
    }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold, BiomeType.Swamp).contains(biomeType))
        texture(position, { decors[Decors.rocks03] }) {
            setScale(1f, 1f)
            centered()
            offset(11.6f, -97.3f)
        }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.iceCrevasse00] }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(94.1f, -5.9f)
        }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.treePineSnow01] }) {
            setScale(1f, 1f)
            centered()
            offset(79.5f, -25.2f)
        }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.treePineSnow00] }) {
            setScale(1f, 1f)
            centered()
            offset(-107f, 13.2f)
        }
    if (listOf(BiomeType.Cold, BiomeType.ExtremeCold).contains(biomeType))
        texture(position, { decors[Decors.iceCrevasse01] }) {
            setScale(0.3f, 0.3f)
            centered()
            offset(-103f, -44.5f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { decors[Decors.jungleTree00] }) {
            setScale(1f, 1f)
            centered()
            offset(100.2f, -21.8f)
        }
    if (listOf(BiomeType.Swamp).contains(biomeType))
        texture(position, { decors[Decors.jungleTree04] }) {
            setScale(1f, 1f)
            centered()
            offset(-102.2f, -37.7f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treeA01] }) {
            setScale(1f, 1f)
            centered()
            offset(111.5f, 52.9f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treeA03] }) {
            setScale(1f, 1f)
            centered()
            offset(-99.5f, -9.5f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.decorPond02] }) {
            setScale(1f, 1f)
            centered()
            offset(86.29995f, -26.09998f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.grass01] }) {
            setScale(1f, 1f)
            centered()
            offset(87f, -56f)
        }
}