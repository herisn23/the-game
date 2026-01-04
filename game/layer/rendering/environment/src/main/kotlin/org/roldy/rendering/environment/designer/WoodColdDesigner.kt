package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.woodcold(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.treePine05] }) {
        setScale(1f, 1f)
        centered()
        offset(10f, 103.3f)
    }
    texture(position, { decors[Decors.treePine03] }) {
        setScale(1f, 1f)
        centered()
        offset(-59.1f, 77.4f)
    }
    texture(position, { decors[Decors.treePineSnow04] }) {
        setScale(1f, 1f)
        centered()
        offset(72.4f, 54.8f)
    }
    texture(position, { decors[Decors.foresterHut02] }) {
        setScale(1f, 1f)
        centered()
        offset(-0.6f, 24.9f)
    }
    texture(position, { decors[Decors.foresterStumps00] }) {
        setScale(1f, 1f)
        centered()
        offset(77.1f, -32.2f)
    }
    texture(position, { decors[Decors.treePine04] }) {
        setScale(1f, 1f)
        centered()
        offset(-75f, 23.6f)
    }
    texture(position, { decors[Decors.treePine05] }) {
        setScale(1f, 1f)
        centered()
        offset(-1.9f, -36.2f)
    }
    texture(position, { decors[Decors.forester_hut01] }) {
        setScale(1f, 1f)
        centered()
        offset(-80.3f, -38.2f)
    }
}