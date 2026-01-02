package org.roldy.rendering.environment.harvestable

import org.roldy.rendering.environment.TileDecorationNormal
import org.roldy.rendering.environment.composite.TextureCompositor


fun TextureCompositor.roughQuartz() =
    texture({
        region<TileDecorationNormal> { mountainFortress }
    }) {

    }
