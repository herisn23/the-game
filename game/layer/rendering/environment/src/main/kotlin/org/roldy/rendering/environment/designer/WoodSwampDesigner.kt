package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.woodswamp(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.swampTree00] }) {
        setScale(1f, 1f)
        centered()
        offset(3.5f, 115.2f)
    }
    texture(position, { decors[Decors.swampTree05] }) {
        setScale(1f, 1f)
        centered()
        offset(-37.4f, 95.6f)
    }
    texture(position, { decors[Decors.swampStump00] }) {
        setScale(-1f, 1f)
        centered()
        offset(-44.1f, 50.2f)
    }
    texture(position, { decors[Decors.swampStump02] }) {
        setScale(1f, 1f)
        centered()
        offset(-0.2f, 35.5f)
    }
    texture(position, { decors[Decors.swampTree02] }) {
        setScale(1f, 1f)
        centered()
        offset(-78.1f, 85.5f)
    }
    texture(position, { decors[Decors.swampStump03] }) {
        setScale(1f, 1f)
        centered()
        offset(-96.8f, 29.5f)
    }
    texture(position, { decors[Decors.swampStump01] }) {
        setScale(1f, 1f)
        centered()
        offset(43.2f, 100.6f)
    }
    texture(position, { decors[Decors.foresterStumps00] }) {
        setScale(1f, 1f)
        centered()
        offset(22.4f, 9.8f)
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
    texture(position, { decors[Decors.swampTree02] }) {
        setScale(-1f, 1f)
        centered()
        offset(84.5f, 44.1f)
    }
    texture(position, { decors[Decors.foresterHut01] }) {
        setScale(1f, 1f)
        centered()
        offset(80.8f, -3.5f)
    }
    texture(position, { decors[Decors.swampTree01] }) {
        setScale(1f, 1f)
        centered()
        offset(-13.4f, -40.1f)
    }
    texture(position, { decors[Decors.decorPond03] }) {
        setScale(1f, 1f)
        centered()
        offset(34.5f, -50.3f)
    }
    texture(position, { decors[Decors.swampStump02] }) {
        setScale(1f, 1f)
        centered()
        offset(34.5f, -26f)
    }
}