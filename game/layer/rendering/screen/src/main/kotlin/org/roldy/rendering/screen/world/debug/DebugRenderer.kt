package org.roldy.rendering.screen.world.debug

import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.rendering.g2d.chunk.ChunkDebugRenderer
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.map.WorldMapDebugRenderer
import org.roldy.rendering.screen.world.chunk.WorldMapChunkManager

class DebugRenderer(
    val camera: OrthographicCamera,
    val map: WorldMap,
    val manager: WorldMapChunkManager
) : AutoDisposableAdapter() {

    val mapDebug by disposable { WorldMapDebugRenderer(map, camera) }
    val chunkDebug by disposable {
        ChunkDebugRenderer(camera, manager)
    }

    fun render() {
        mapDebug.render()
        chunkDebug.render()
    }
}