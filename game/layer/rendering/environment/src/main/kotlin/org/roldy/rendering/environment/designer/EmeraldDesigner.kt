package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.emerald(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.decorMountain02] }) {
        setScale(1f, 1f)
        centered()
        offset(-2.2f, 55.7f)
    }
    texture(position, { decors[Decors.mines00] }) {
        setScale(1f, 1f)
        centered()
        offset(-16.9f, -23.6f)
    }
    texture(position, { decors[Decors.decorPond02] }) {
        setScale(1f, 1f)
        centered()
        offset(30.4f, -70.9f)
    }
    texture(position, { decors[Decors.grass01] }) {
        setScale(1f, 1f)
        centered()
        offset(-25.3f, -80.9f)
    }
    texture(position, { decors[Decors.barrels00] }) {
        setScale(1f, 1f)
        centered()
        offset(-64.2f, -34.7f)
    }
}