package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.woodforest(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.foresterShed00] }) {
        setScale(1f, 1f)
        centered()
        offset(-15.2f, 102.6f)
    }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treesA_cluster00] }) {
            setScale(1f, 1f)
            centered()
            offset(-51.1f, 68.1f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treesA_cluster01] }) {
            setScale(1f, 1f)
            centered()
            offset(-80.4f, 14.3f)
        }
    texture(position, { decors[Decors.foresterStumps00] }) {
        setScale(1f, 1f)
        centered()
        offset(-9.2f, 32.2f)
    }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.decorTreeCluster01] }) {
            setScale(1f, 1f)
            centered()
            offset(65.1f, 36.9f)
        }
    texture(position, { decors[Decors.forester_hut02] }) {
        setScale(1f, 1f)
        centered()
        offset(-33.1f, -9f)
    }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treesA_cluster01] }) {
            setScale(1f, 1f)
            centered()
            offset(37.9f, -36.2f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treeA02] }) {
            setScale(1f, 1f)
            centered()
            offset(-5.9f, -54.1f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treeC02] }) {
            setScale(1f, 1f)
            centered()
            offset(-41.8f, -55.4f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.well04] }) {
            setScale(1f, 1f)
            centered()
            offset(87f, -54.1f)
        }
    if (listOf(BiomeType.Forest).contains(biomeType))
        texture(position, { decors[Decors.treeB02] }) {
            setScale(1f, 1f)
            centered()
            offset(13.9f, -90f)
        }
}