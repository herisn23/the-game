package org.roldy.rendering.environment.harvestable

import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.mine.harvestable.Gem
import org.roldy.data.mine.harvestable.Harvestable
import org.roldy.rendering.environment.TileDecorationAtlas
import org.roldy.rendering.environment.TileDecorationNormal
import org.roldy.rendering.environment.composite.CompositeTexture
import org.roldy.rendering.environment.composite.TextureCompositor

fun TileDecorationAtlas.composite(harvestable: Harvestable, biome: BiomeType, tileSize: Float): List<CompositeTexture> =
    TextureCompositor(this, tileSize).apply {
        when (harvestable) {
            Gem.RoughQuartz -> roughQuartz()
            Gem.Amber -> amber(biome)
            else -> texture({
                region<TileDecorationNormal> { mines05 }
            }) {
                center()
                parent().center()
            }
        }
    }.retrieve()


fun BiomeType.isCold() =
    when (this) {
        Cold, ExtremeCold -> true
        else -> false
    }

fun BiomeType.isTropic() =
    when (this) {
        Jungle, Swamp -> true
        else -> false
    }

fun BiomeType.isDesert() =
    when (this) {
        Desert, DeepDesert, Savanna -> true
        else -> false
    }