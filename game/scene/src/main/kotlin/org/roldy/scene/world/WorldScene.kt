package org.roldy.scene.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.InputProcessorDelegate
import org.roldy.core.asset.loadAsset
import org.roldy.core.stream.WorldStreamer
import org.roldy.core.stream.chunk.ChunkItemData
import org.roldy.environment.EnvironmentalObject
import org.roldy.keybind.keybinds
import org.roldy.map.WorldMap
import org.roldy.map.WorldMapChunkDataManager
import org.roldy.map.WorldMapSize
import org.roldy.pawn.Pawn
import org.roldy.pawn.PawnInputProcessor

class WorldScene(
    camera: OrthographicCamera
) {

    val batch = SpriteBatch()
    val map = WorldMap(WorldMapSize.Small, 1, camera)
    val atlas = TextureAtlas(loadAsset("Trees.atlas"))
    val chunkManager = WorldMapChunkDataManager(512f, map.terrainData) { coords, chunkSize ->
        val items = mutableListOf<ChunkItemData>()
        repeat(1) {
            val x = coords.cx * chunkSize + (Math.random() * chunkSize).toFloat()
            val y = coords.cy * chunkSize + (Math.random() * chunkSize).toFloat()
            items += ChunkItemData(type = atlas.regions.random().name, x = x, y = y)
        }
        items
    }
    val playerPawn = Pawn(camera, batch)
    val streamer = WorldStreamer(camera, chunkManager, listOf(playerPawn)) {
        EnvironmentalObject(atlas)
    }


    context(delta: Float)
    fun render() {
        map.render()
        streamer.update()
        streamer.render(batch)
    }

    init {
        Gdx.input.inputProcessor = InputProcessorDelegate(
            listOf(
                map.inputProcessor,
                PawnInputProcessor(keybinds) {
                    playerPawn
                }
            )
        )
    }
}