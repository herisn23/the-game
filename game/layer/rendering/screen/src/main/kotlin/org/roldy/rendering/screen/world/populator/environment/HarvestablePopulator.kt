package org.roldy.rendering.screen.world.populator.environment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.data.state.HarvestableState
import org.roldy.gui.CraftingIconTextures
import org.roldy.gui.CraftingIconTexturesType
import org.roldy.rendering.environment.TileObject
import org.roldy.rendering.environment.composite.SpriteCompositor
import org.roldy.rendering.environment.composite.SpritePool
import org.roldy.rendering.environment.harvestable.MapAtlas
import org.roldy.rendering.environment.harvestable.composite
import org.roldy.rendering.environment.item.HarvestableTileBehaviour
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.chunk.WorldMapChunk
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator

class HarvestablePopulator(
    override val map: WorldMap,
    val harvestable: List<HarvestableState>,
    decorationAtlas: TextureAtlas,
    tilesAtlas: TextureAtlas,
    craftingIconsAtlas: TextureAtlas
) : AutoDisposableAdapter(), WorldChunkPopulator {
    val craftingIcons = CraftingIconTextures(craftingIconsAtlas)
    val mapAtlas = MapAtlas(decorationAtlas, tilesAtlas)
    val pool = SpritePool()
    val compositor = SpriteCompositor(pool, mapAtlas, map.data.tileSize.toFloat())
    override fun populate(
        chunk: WorldMapChunk,
        existingObjects: List<TileObject.Data>
    ): List<TileObject.Data> {
        val data = chunk.terrainData()
        val harvestableInChunk = harvestable.filter { harvest ->
            data.contains(harvest.coords)
        }
        return harvestableInChunk.map { harvestable ->
            val position = worldPosition(harvestable.coords)
            val biome = data.getValue(harvestable.coords).terrain.biome.data.type
            val tileSize = map.data.tileSize.toFloat()
            HarvestableTileBehaviour.Data(
                position = position,
                coords = harvestable.coords,
                icon = craftingIcons.region(harvestable.harvestable, CraftingIconTexturesType.Normal),
                sprites = compositor.composite(position, harvestable.harvestable, biome),
                tileSize = tileSize
            ) {
                sprites.forEach(pool::free)
            }
        }
    }

}