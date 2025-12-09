package org.roldy.scene.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.InputProcessorDelegate
import org.roldy.core.WorldPositioned
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
import org.roldy.scene.world.debug.DebugRenderer
import org.roldy.scene.world.pathfinding.AsyncPathfindingProxy
import org.roldy.scene.world.pathfinding.Pathfinder
import org.roldy.scene.world.populator.WorldMapPopulator

class WorldScene(
    private val camera: OrthographicCamera
) : AutoDisposableAdapter(), Scene {
    val debugEnabled = false
    val mapSize = WorldMapSize.Small
    val tileSize = 256

    val zoom = ZoomCameraProcessor(keybinds)
    val seed = 1L
    val batch by disposable { SpriteBatch() }


    val map by disposable {
        WorldMap(
            camera,
            zoom,
            seed,
            mapSize,
            tileSize
        )
    }

    val pathfinder = Pathfinder(map)

    val populator by disposable { WorldMapPopulator(map, pathfinder) }

    val currentPawn by disposable {
        PawnFigure(batch).apply {
            val center = mapSize.size / 2 x mapSize.size / 2
            coords = center
            position = map.tilePosition.resolve(coords)
        }
    }
    val chunkManager = WorldMapChunkManager(
        map,
        populator
    )
    val chunkRenderer = ChunkRenderer(
        camera,
        listOf(currentPawn),
        chunkManager
    )
    val debugRenderer by disposable { DebugRenderer(camera, map, chunkManager) }
    val pathfinderProxy = AsyncPathfindingProxy(pathfinder, {
        currentPawn.coords
    }) { path ->
        currentPawn.pathWalking(path.tiles)
    }
    val objectMove = ObjectMoveInputProcessor(keybinds, map, camera, pathfinderProxy::findPath)
    val mapInputProcessor = WorldMapInputProcessor(listOf(zoom, objectMove))


    context(delta: Float)
    override fun render() {
        clampMovableObjectsToMapBounds()
        camera.position.set(currentPawn.position.x, currentPawn.position.y, 0f)
        camera.update()
        map.render()
        chunkRenderer.render(batch)
        if (debugEnabled)
            debugRenderer.render()
    }

    private fun clampMovableObjectsToMapBounds() {
        chunkRenderer.objects.forEach {
            if (it is WorldPositioned) {
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