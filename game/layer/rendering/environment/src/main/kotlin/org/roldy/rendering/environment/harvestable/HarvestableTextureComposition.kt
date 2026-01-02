package org.roldy.rendering.environment.harvestable

import org.roldy.data.mine.harvestable.Gem
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.rendering.environment.TileDecorationAtlas
import org.roldy.rendering.environment.TileDecorationNormal
import org.roldy.rendering.environment.composite.CompositeTexture
import org.roldy.rendering.environment.composite.TextureCompositor

fun TileDecorationAtlas.composite(harvestable: Harvestable, tileSize: Float): List<CompositeTexture> =
    TextureCompositor(this, tileSize).apply {
        when (harvestable) {
            Gem.RoughQuartz -> roughQuartz()
            else -> texture({
                region<TileDecorationNormal> { mines05 }
            }) {
                center()
                parent().center()
            }
        }
    }.retrieve()