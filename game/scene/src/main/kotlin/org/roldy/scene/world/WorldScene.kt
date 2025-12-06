package org.roldy.scene.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.InputProcessorDelegate
import org.roldy.core.renderer.ChunkRenderer
import org.roldy.environment.MapObject
import org.roldy.keybind.keybinds
import org.roldy.map.WorldMap
import org.roldy.map.WorldMapSize
import org.roldy.map.input.WorldMapInputProcessor
import org.roldy.pawn.Pawn
import org.roldy.pawn.PawnInputProcessor
import org.roldy.scene.Scene
import org.roldy.scene.world.chunk.WorldMapChunkManager
import org.roldy.scene.world.chunk.WorldPopulator
import org.roldy.terrain.ProceduralMapGenerator

class WorldScene(
    private val camera: OrthographicCamera
) : Scene {
    val mapSize = WorldMapSize.Debug
    val batch = SpriteBatch()
    val tileSize = 200

    val pawnInputProcessor = PawnInputProcessor(keybinds) { currentPawn }
    val mapInputProcessor = WorldMapInputProcessor(keybinds)

    val seed = 1L
    val map = WorldMap(
        camera,
        mapInputProcessor,
        ProceduralMapGenerator(
            seed = seed,
            width = mapSize.size,
            height = mapSize.size,
            tileSize = tileSize,
            enableTransitions = true,    // Enable transitions
        )
    )
    val playerPawn = Pawn(batch)

    val currentPawn = playerPawn
    val chunkRenderer = ChunkRenderer(
        camera,
        WorldMapChunkManager(
            tileSize,
            mapSize,
            WorldPopulator(map.terrainData, mapSize, seed)
        ) {
            MapObject()
        },
        listOf(playerPawn)
    )


    context(delta: Float)
    override fun render() {
        camera.position.set(currentPawn.manager.x, currentPawn.manager.y, 0f)
        camera.update()
        map.render()
        chunkRenderer.render(batch)
    }


    override fun onShow() {
        Gdx.input.inputProcessor = InputProcessorDelegate(
            listOf(
                mapInputProcessor,
                pawnInputProcessor
            )
        )
    }

    override fun onHide() {
        Gdx.input.inputProcessor = null
    }
}