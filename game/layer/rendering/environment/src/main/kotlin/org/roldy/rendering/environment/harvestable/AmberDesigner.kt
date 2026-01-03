package org.roldy.rendering.environment.harvestable

import org.roldy.core.utils.get
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.TextureCompositor
import org.roldy.rendering.tiles.Decors


fun TextureCompositor.amber(biomeType: BiomeType) {
    texture({ decors[Decors.decorMountain02] }) {
        centered()
        offset(-2.2f, 55.7f)
    }
    texture({ decors[Decors.mines00] }) {
        centered()
        offset(-21.4f, -30.3f)
    }
    texture({ decors[Decors.decorPond02] }) {
        centered()
        offset(30.4f, -70.9f)
    }
    texture({ decors[Decors.grass01] }) {
        centered()
        offset(-25.3f, -80.9f)
    }
    texture({ decors[Decors.barrels00] }) {
        centered()
        offset(-64.2f, -34.7f)
    }
}