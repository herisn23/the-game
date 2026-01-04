package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.orevolcanic(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.lavaCone03] }) {
        setScale(1f, 1f)
        centered()
        offset(2.6f, 97.4f)
    }
    texture(position, { decors[Decors.lavaConeDormant03] }) {
        setScale(1f, 1f)
        centered()
        offset(39.7f, 58.2f)
    }
    texture(position, { decors[Decors.fumarole02] }) {
        setScale(1f, 1f)
        centered()
        offset(-55.7f, 68.2f)
    }
    texture(position, { decors[Decors.lavaConeDormant03] }) {
        setScale(1.6f, 1.6f)
        centered()
        offset(-4f, 5.1f)
    }
    texture(position, { decors[Decors.lavaConeDormant00] }) {
        setScale(1f, 1f)
        centered()
        offset(92.1f, 17.8f)
    }
    texture(position, { decors[Decors.lavaCone00] }) {
        setScale(1f, 1f)
        centered()
        offset(-80.3f, 48.2f)
    }
    texture(position, { decors[Decors.mines01] }) {
        setScale(1f, 1f)
        centered()
        offset(39.8f, -36.1f)
    }
    texture(position, { decors[Decors.burnedTree00] }) {
        setScale(1f, 1f)
        centered()
        offset(69.7f, -4.8f)
    }
    texture(position, { decors[Decors.lavaOutcrop01] }) {
        setScale(1f, 1f)
        centered()
        offset(-5.3f, -42.6f)
    }
    texture(position, { decors[Decors.fumarole00] }) {
        setScale(1f, 1f)
        centered()
        offset(-87.6f, -30.7f)
    }
}