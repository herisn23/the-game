package org.roldy.rendering.environment.harvestable

import com.badlogic.gdx.math.Vector2
import org.roldy.core.utils.get
import org.roldy.data.map.Terrain
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.tiles.Environment

fun SpriteCompositor.fiber(position: Vector2, terrain: Terrain, harvestable: Harvestable, empty: () -> Boolean) {
    if ("blue_cold" == terrain.data.groupKey)
        texture(position, { environment[Environment.tree_3_13] }, { !empty() }) {
            setScale(1f, 1f)
            centered()
            offset(-72.6f, 105.1f)
        }
}