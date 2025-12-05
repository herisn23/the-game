package org.roldy.scene.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.InputProcessorDelegate
import org.roldy.core.asset.loadAsset
import org.roldy.core.renderer.ChunkRenderer
import org.roldy.environment.EnvironmentalObject
import org.roldy.keybind.keybinds
import org.roldy.map.WorldMap
import org.roldy.map.WorldMapChunkDataManager
import org.roldy.map.WorldMapSize
import org.roldy.pawn.Pawn
import org.roldy.pawn.PawnInputProcessor
import org.roldy.terrain.ProceduralMapGenerator

class WorldScene(
    private val camera: OrthographicCamera
) {
    val mapSize = WorldMapSize.Large
    val batch = SpriteBatch()
    val tileSize = 200

    val map = WorldMap(
        camera, ProceduralMapGenerator(
            seed = 1,
            width = mapSize.size,
            height = mapSize.size,
            tileSize = tileSize,
            enableTransitions = true,    // Enable transitions
            debugMode = false             // Enable debug mode
        )
    )
    val atlas = TextureAtlas(loadAsset("Trees.atlas"))
    val playerPawn = Pawn(batch)

    val currentPawn = playerPawn
    val chunkRenderer = ChunkRenderer(
        camera,
        WorldMapChunkDataManager(
            tileSize,
            mapSize,
            WorldPopulator(atlas, map.terrainData)
        ),
        listOf(playerPawn)
    ) {
        EnvironmentalObject(atlas)
    }


    context(delta: Float)
    fun render() {
        camera.position.set(currentPawn.manager.x, currentPawn.manager.y, 0f)
        camera.update()
        map.render()
        chunkRenderer.update()
        chunkRenderer.render(batch)
    }

    init {
        Gdx.input.inputProcessor = InputProcessorDelegate(
            listOf(
                map.inputProcessor,
                PawnInputProcessor(keybinds) {
                    currentPawn
                }
            )
        )
    }
}