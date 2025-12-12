package org.roldy.rendering.screen.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.InputProcessorDelegate
import org.roldy.core.keybind.keybinds
import org.roldy.core.pathwalker.AsyncPathfindingProxy
import org.roldy.core.pathwalker.FindPath
import org.roldy.core.x
import org.roldy.data.pawn.PawnData
import org.roldy.data.tile.walkCost
import org.roldy.rendering.g2d.Diagnostics
import org.roldy.rendering.g2d.chunk.ChunkRenderer
import org.roldy.rendering.g2d.disposable.AutoDisposableScreenAdapter
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.pawn.PawnFigure
import org.roldy.rendering.screen.world.chunk.WorldMapChunkManager
import org.roldy.rendering.screen.world.debug.DebugRenderer
import org.roldy.rendering.screen.world.input.ObjectMoveInputProcessor
import org.roldy.rendering.screen.world.input.ZoomInputProcessor
import org.roldy.rendering.screen.world.populator.WorldMapPopulator

class WorldScreen(
    private val camera: OrthographicCamera,
    private val map: WorldMap,
    populator: WorldMapPopulator,
    findPath: FindPath,
    private val debugEnabled: Boolean = false,
) : AutoDisposableScreenAdapter() {
    val batch by disposable { SpriteBatch() }
    val diagnostics by disposable { Diagnostics() }

    val zoom = ZoomInputProcessor(keybinds, camera, 1f, 10f)

    val pathfinderProxy = AsyncPathfindingProxy(findPath, {
        currentPawn.coords
    }) { path ->
        currentPawn.pathWalking(path)
    }

    val currentPawn: PawnFigure by disposable {
        PawnFigure(batch, { PawnData() }) {
            //TODO this code shouldn't be here
            val objects = chunkManager.tileData(it)
            (objects + listOfNotNull(map.terrainData[it])).walkCost()
        }.apply {
            val center = map.data.size.width / 2 x map.data.size.height / 2
            coords = center
            position = map.tilePosition.resolve(coords)
        }
    }
    val chunkManager by disposable {
        WorldMapChunkManager(
            map,
            populator,
            listOf(currentPawn)
        )
    }

    val chunkRenderer = ChunkRenderer(
        camera,
        chunkManager
    )
    val debugRenderer by disposable { DebugRenderer(camera, map, chunkManager) }

    override fun resize(width: Int, height: Int) {
        diagnostics.resize(width, height)
    }

    override fun render(delta: Float) {
        context(delta) {
            zoom.update()
            camera.position.set(currentPawn.position.x, currentPawn.position.y, 0f)
            camera.update()
            map.render()
            chunkRenderer.render(batch)
            if (debugEnabled) {
                debugRenderer.render()
            }
            diagnostics.render()
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = InputProcessorDelegate(
            listOf(
                zoom,
                ObjectMoveInputProcessor(keybinds, map, camera, pathfinderProxy::findPath)
            )
        )
        chunkManager.pause = false
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
        chunkManager.pause = true
    }

}