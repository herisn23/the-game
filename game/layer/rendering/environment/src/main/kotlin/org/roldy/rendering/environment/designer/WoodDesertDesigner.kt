package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.wooddesert(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.cactus01] }) {
        setScale(1f, 1f)
        centered()
        offset(-75.7f, 87.4f)
    }
    texture(position, { decors[Decors.cactus01] }) {
        setScale(1f, 1f)
        centered()
        offset(107.6f, 62.8f)
    }
    texture(position, { decors[Decors.cactus08] }) {
        setScale(1f, 1f)
        centered()
        offset(-39.8f, 111.9f)
    }
    texture(position, { decors[Decors.cactus08] }) {
        setScale(-1f, 1f)
        centered()
        offset(-71f, -46.2f)
    }
    texture(position, { decors[Decors.cactus02] }) {
        setScale(1f, 1f)
        centered()
        offset(41.1f, 100.6f)
    }
    texture(position, { decors[Decors.forester_hut02] }) {
        setScale(1f, 1f)
        centered()
        offset(52.5f, -16.2f)
    }
    texture(position, { decors[Decors.forester_shed00] }) {
        setScale(1f, 1f)
        centered()
        offset(-55.7f, 50.2f)
    }
    texture(position, { decors[Decors.cactus07] }) {
        setScale(1f, 1f)
        centered()
        offset(-32.5f, -54.1f)
    }
    texture(position, { decors[Decors.yellowRock01] }) {
        setScale(1f, 1f)
        centered()
        offset(54.5f, 48.8f)
    }
    texture(position, { decors[Decors.cactus00] }) {
        setScale(1f, 1f)
        centered()
        offset(-97f, -12.9f)
    }
    texture(position, { decors[Decors.yellowRockSmall00] }) {
        setScale(1f, 1f)
        centered()
        offset(3.3f, 93.4f)
    }
    texture(position, { decors[Decors.cactus00] }) {
        setScale(-1f, 1f)
        centered()
        offset(-51.2f, 17f)
    }
    texture(position, { decors[Decors.cactus01] }) {
        setScale(-1f, 1f)
        centered()
        offset(54.5f, -40.8f)
    }
    texture(position, { decors[Decors.cowSkull00] }) {
        setScale(-1f, 1f)
        centered()
        offset(-15.9f, 17f)
    }
    texture(position, { decors[Decors.cactus02] }) {
        setScale(1f, 1f)
        centered()
        offset(-16.7f, 4.9f)
    }
    texture(position, { decors[Decors.cactus01] }) {
        setScale(1f, 1f)
        centered()
        offset(8f, -61.4f)
    }
    texture(position, { decors[Decors.cactus04] }) {
        setScale(1f, 1f)
        centered()
        offset(9.3f, 28.3f)
    }
    texture(position, { decors[Decors.yellowRockSmall04] }) {
        setScale(1f, 1f)
        centered()
        offset(-31.9f, -78.6f)
    }
}