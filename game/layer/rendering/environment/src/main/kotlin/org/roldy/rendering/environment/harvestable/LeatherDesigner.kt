package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.map.Terrain
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.rendering.environment.composite.CompositeAnimation
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Environment

fun SpriteCompositor.leather(position: Vector2, terrain: Terrain, harvestable: Harvestable, empty: () -> Boolean) {
    if ("blue_cold" == terrain.data.groupKey)
        texture(
            position,
            { environment[Environment.grass_0000s_0004_Layer_88] },
            { !empty() },
            CompositeAnimation(Scale, 0.008f, 0.03f, 3f)
        ) {
            setScale(0.5f, 0.5f)
            centered()
            offset(5.2f, 72.7f)
        }
}