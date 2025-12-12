package org.roldy.rendering.scene.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.WorldPositioned
import org.roldy.core.x
import org.roldy.rendering.g2d.chunk.ChunkRenderer
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.pawn.PawnFigure
import org.roldy.rendering.scene.Scene
import org.roldy.rendering.scene.world.chunk.WorldMapChunkManager
import org.roldy.rendering.scene.world.debug.DebugRenderer
import org.roldy.rendering.scene.world.populator.WorldMapPopulator

class WorldScene(
    private val camera: OrthographicCamera,
    private val map: WorldMap,
    private val inputProcessor: InputAdapter,
    populator: WorldMapPopulator,
    private val debugEnabled: Boolean = false
) : AutoDisposableAdapter(), Scene {
    val batch by disposable { SpriteBatch() }

    val currentPawn by disposable {
        PawnFigure(batch).apply {
            val center = map.data.size.width / 2 x map.data.size.height / 2
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
        chunkManager,
        listOf(currentPawn)
    )
    val debugRenderer by disposable { DebugRenderer(camera, map, chunkManager) }

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
        //TODO this method should be removed?
        chunkRenderer.objects.forEach {
            if (it is WorldPositioned) {
                map.clampToBounds(it.position) { position ->
                    it.position = position
                }
            }
        }

    }

    override fun onShow() {
        Gdx.input.inputProcessor = inputProcessor
    }

    override fun onHide() {
        Gdx.input.inputProcessor = null
    }

}