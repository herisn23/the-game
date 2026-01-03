package org.roldy.rendering.environment.harvestable

import org.roldy.core.utils.get
import org.roldy.rendering.environment.composite.TextureCompositor
import org.roldy.rendering.tiles.Decors


fun TextureCompositor.roughQuartz() =
    texture({
        decors[Decors.mountainFortress]
    }) {

    }
