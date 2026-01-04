package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.woodvolcanic(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.forester_hut02] }) {
        setScale(1f, 1f)
        centered()
        offset(-58f, -6.5f)
    }
    texture(position, { decors[Decors.lavaCone02] }) {
        setScale(1f, 1f)
        centered()
        offset(-50.4f, 85.9f)
    }
    texture(position, { decors[Decors.burnedTree00] }) {
        setScale(1f, 1f)
        centered()
        offset(-1.7f, 109.1f)
    }
    texture(position, { decors[Decors.burnedTree05] }) {
        setScale(1f, 1f)
        centered()
        offset(-14.4f, 68.6f)
    }
    texture(position, { decors[Decors.lavaRocks01] }) {
        setScale(1f, 1f)
        centered()
        offset(-24.6f, 41.7f)
    }
    texture(position, { decors[Decors.burnedTree03] }) {
        setScale(1f, 1f)
        centered()
        offset(14f, 16.9f)
    }
    texture(position, { decors[Decors.burnedTree07] }) {
        setScale(1f, 1f)
        centered()
        offset(-71.2f, 56.5f)
    }
    texture(position, { decors[Decors.foresterStumps01] }) {
        setScale(1f, 1f)
        centered()
        offset(46.6f, 16.4f)
    }
    texture(position, { decors[Decors.lavaRocks04] }) {
        setScale(1f, 1f)
        centered()
        offset(101.4f, 46.3f)
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
    texture(position, { decors[Decors.burnedTree01] }) {
        setScale(1f, 1f)
        centered()
        offset(19.5f, -37.3f)
    }
    texture(position, { decors[Decors.burnedTree02] }) {
        setScale(1f, 1f)
        centered()
        offset(-59.6f, -53f)
    }
    texture(position, { decors[Decors.burnedTree02] }) {
        setScale(1f, 1f)
        centered()
        offset(-105.3f, 47.4f)
    }
    texture(position, { decors[Decors.crater03] }) {
        setScale(1f, 1f)
        centered()
        offset(0.3f, -81.1f)
    }
    texture(position, { decors[Decors.fumarole03] }) {
        setScale(1f, 1f)
        centered()
        offset(68.8f, -61.2f)
    }
}