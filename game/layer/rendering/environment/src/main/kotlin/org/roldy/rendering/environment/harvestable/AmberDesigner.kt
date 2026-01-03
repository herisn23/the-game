package org.roldy.rendering.environment.harvestable

import org.roldy.data.configuration.biome.BiomeType
import org.roldy.rendering.environment.composite.TextureCompositor


fun TextureCompositor.amber(biomeType: BiomeType) {
    normal({ archeryRange00 }) {
        centered()
        offset(128f, 128f)
    }
}