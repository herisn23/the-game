package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.woodjungle(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.jungleTree00] }) {
        setScale(1f, 1f)
        centered()
        offset(3.5f, 115.2f)
    }
    texture(position, { decors[Decors.jungleTree05] }) {
        setScale(1f, 1f)
        centered()
        offset(-37.4f, 95.6f)
    }
    texture(position, { decors[Decors.junglePalm00] }) {
        setScale(-1f, 1f)
        centered()
        offset(-52.2f, 66.4f)
    }
    texture(position, { decors[Decors.junglePalm01] }) {
        setScale(1f, 1f)
        centered()
        offset(-7.8f, 45.6f)
    }
    texture(position, { decors[Decors.jungleTree00] }) {
        setScale(1f, 1f)
        centered()
        offset(-87.7f, 85f)
    }
    texture(position, { decors[Decors.junglePalm01] }) {
        setScale(1f, 1f)
        centered()
        offset(-86.2f, 34f)
    }
    texture(position, { decors[Decors.junglePalm03] }) {
        setScale(1f, 1f)
        centered()
        offset(47.2f, 106.2f)
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
    texture(position, { decors[Decors.jungleTree02] }) {
        setScale(1f, 1f)
        centered()
        offset(84.5f, 44.1f)
    }
    texture(position, { decors[Decors.foresterHut01] }) {
        setScale(1f, 1f)
        centered()
        offset(80.8f, -3.5f)
    }
    texture(position, { decors[Decors.jungleTree00] }) {
        setScale(1f, 1f)
        centered()
        offset(-19.5f, -44.2f)
    }
    texture(position, { decors[Decors.decorPond03] }) {
        setScale(1f, 1f)
        centered()
        offset(34.5f, -50.3f)
    }
    texture(position, { decors[Decors.junglePalm02] }) {
        setScale(1f, 1f)
        centered()
        offset(50.7f, -36.6f)
    }
}