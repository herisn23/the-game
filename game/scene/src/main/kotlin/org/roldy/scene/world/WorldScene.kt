package org.roldy.scene.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.InputProcessorDelegate
import org.roldy.core.Placeable
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.keybind.keybinds
import org.roldy.core.renderer.ChunkRenderer
import org.roldy.core.x
import org.roldy.map.WorldMap
import org.roldy.map.WorldMapSize
import org.roldy.map.input.ObjectMoveInputProcessor
import org.roldy.map.input.WorldMapInputProcessor
import org.roldy.map.input.ZoomCameraProcessor
import org.roldy.pawn.PawnFigure
import org.roldy.scene.Scene
import org.roldy.scene.world.chunk.WorldMapChunkManager
import org.roldy.scene.world.pathfinding.PathfinderManager
import org.roldy.scene.world.populator.WorldPopulator
import org.roldy.terrain.ProceduralMapGenerator

class WorldScene(
    private val camera: OrthographicCamera
) : AutoDisposableAdapter(), Scene {
    val mapSize = WorldMapSize.Debug
    val tileSize = 256

    val zoom = ZoomCameraProcessor(keybinds)
    val seed = 1L
    val batch by disposable { SpriteBatch() }

    val map by disposable {
        WorldMap(
            camera,
            zoom,
            ProceduralMapGenerator(
                seed = seed,
                width = mapSize.size,
                height = mapSize.size,
                tileSize = tileSize,
                moistureScale = mapSize.moistureScale,
                temperatureScale = mapSize.temperatureScale,
                elevationScale = mapSize.elevationScale
            )
        )
    }
    val populator by disposable { WorldPopulator(map.terrainData, mapSize, seed) }

    val currentPawn by disposable {
        PawnFigure(batch).apply {
            val centerX = (mapSize.size * tileSize) / 2f
            val centerY = (mapSize.size * tileSize) / 3f
            val center = centerX x centerY
            map.tilePosition.resolve(center)?.let { tileCoords ->
                coords = tileCoords
                position = map.tilePosition.resolve(tileCoords)
            }
        }
    }
    val chunkRenderer = ChunkRenderer(
        camera,
        listOf(currentPawn),
        WorldMapChunkManager(
            tileSize,
            mapSize,
            populator
        )
    )

    val objectMove = ObjectMoveInputProcessor(keybinds, map, camera) {
        PathfinderManager(map, {
            currentPawn.coords
        }) { path ->
            currentPawn.pathWalking(path.tiles)
        }
    }
    val mapInputProcessor = WorldMapInputProcessor(listOf(zoom, objectMove))


    context(delta: Float)
    override fun render() {
        clampMovableObjectsToMapBounds()
        camera.position.set(currentPawn.position.x, currentPawn.position.y, 0f)
        camera.update()
        map.render()
        chunkRenderer.render(batch)
    }

    private fun clampMovableObjectsToMapBounds() {
        chunkRenderer.objects.forEach {
            if (it is Placeable) {
                map.clampToBounds(it.position) { position ->
                    it.position = position
                }
            }
        }

    }


    override fun onShow() {
        Gdx.input.inputProcessor = InputProcessorDelegate(
            listOf(
                mapInputProcessor
            )
        )
    }

    override fun onHide() {
        Gdx.input.inputProcessor = null
    }

}