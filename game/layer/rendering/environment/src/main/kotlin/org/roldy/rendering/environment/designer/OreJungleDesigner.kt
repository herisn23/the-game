package org.roldy.rendering.environment.designer

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Decors

fun SpriteCompositor.orejungle(position: Vector2, biomeType: BiomeType) {
    texture(position, { decors[Decors.junglePalm02] }) {
        setScale(1f, 1f)
        centered()
        offset(-55f, 104.3f)
    }
    texture(position, { decors[Decors.palm02] }) {
        setScale(-1f, 1f)
        centered()
        offset(92.3f, 75.1f)
    }
    texture(position, { decors[Decors.yellowRockBig00] }) {
        setScale(1f, 1f)
        centered()
        offset(-1.9f, 92.5f)
    }
    texture(position, { decors[Decors.yellowRockBig01] }) {
        setScale(-1f, 1f)
        centered()
        offset(-55.8f, 40.8f)
    }
    texture(position, { decors[Decors.mines00] }) {
        setScale(1f, 1f)
        centered()
        offset(-35.20002f, 6.199975f)
    }
    texture(position, { decors[Decors.yellowRockBig01] }) {
        setScale(1f, 1f)
        centered()
        offset(60.5f, 55.3f)
    }
    texture(position, { decors[Decors.mines03] }) {
        setScale(-0.8f, 0.8f)
        centered()
        offset(74.4f, 12.1f)
    }
    texture(position, { decors[Decors.yellowRockBig04] }) {
        setScale(1f, 1f)
        centered()
        offset(15.89993f, 34.1f)
    }
    texture(position, { decors[Decors.yellowRockBig02] }) {
        setScale(1f, 1f)
        centered()
        offset(34.49998f, 26.69998f)
    }
    texture(position, { decors[Decors.yellowRockBig03] }) {
        setScale(1f, 1f)
        centered()
        offset(-1.900073f, -2.5f)
    }
    texture(position, { decors[Decors.stiltHouse00] }) {
        setScale(1f, 1f)
        centered()
        offset(-33.8f, -48.3f)
    }
    texture(position, { decors[Decors.stiltHouse04] }) {
        setScale(-1f, 1f)
        centered()
        offset(51.2f, -37f)
    }
    texture(position, { decors[Decors.palm01] }) {
        setScale(-1f, 1f)
        centered()
        offset(103f, -17.8f)
    }
}