package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.woodsavanna(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.desertTree01] }) {
        setScale(1f, 1f)
        centered()
        offset(1.9f, 110.6f)
    }
    texture(position, { decors[Decors.desertTree04] }) {
        setScale(1f, 1f)
        centered()
        offset(-103.9f, 46.7f)
    }
    texture(position, { decors[Decors.desertShrub02] }) {
        setScale(-1f, 1f)
        centered()
        offset(-9.9f, 51.3f)
    }
    texture(position, { decors[Decors.desertTree03] }) {
        setScale(-1f, 1f)
        centered()
        offset(-65.1f, 90.1f)
    }
    texture(position, { decors[Decors.desertTree01] }) {
        setScale(1f, 1f)
        centered()
        offset(-36f, 57.9f)
    }
    texture(position, { decors[Decors.foresterStumps01] }) {
        setScale(1f, 1f)
        centered()
        offset(12.6f, 18.1f)
    }
    texture(position, { decors[Decors.desertShrub01] }) {
        setScale(-1f, 1f)
        centered()
        offset(-10.9f, 8.4f)
    }
    texture(position, { decors[Decors.forester_hut02] }) {
        setScale(1f, 1f)
        centered()
        offset(-58f, -6.5f)
    }
    texture(position, { decors[Decors.forester_shed00] }) {
        setScale(1f, 1f)
        centered()
        offset(49.6f, 71.7f)
    }
    texture(position, { decors[Decors.foresterHut01] }) {
        setScale(1f, 1f)
        centered()
        offset(80.8f, -3.5f)
    }
    texture(position, { decors[Decors.desertTree00] }) {
        setScale(1f, 1f)
        centered()
        offset(53.1f, 44.6f)
    }
    texture(position, { decors[Decors.desertTree02] }) {
        setScale(-1f, 1f)
        centered()
        offset(108.8f, 57.5f)
    }
    texture(position, { decors[Decors.desertTree01] }) {
        setScale(1f, 1f)
        centered()
        offset(33.1f, -25.9f)
    }
    texture(position, { decors[Decors.desertTree04] }) {
        setScale(-1f, 1f)
        centered()
        offset(-5.8f, -62.3f)
    }
    texture(position, { decors[Decors.redRock03] }) {
        setScale(1f, 1f)
        centered()
        offset(-62.6f, -61.8f)
    }
    texture(position, { decors[Decors.redRockSmall00] }) {
        setScale(-1f, 1f)
        centered()
        offset(37.1f, -76.6f)
    }
    texture(position, { decors[Decors.desertShrub00] }) {
        setScale(1f, 1f)
        centered()
        offset(83.6f, -60.8f)
    }
}