package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.roughquartz(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.clayPit00] }) {
        setScale(1f, 1f)
        centered()
        offset(0f, 0f)
    }
    texture(position, { decors[Decors.mines03] }) {
        setScale(0.5f, 0.5f)
        centered()
        offset(-26.9f, 4.7f)
    }
}