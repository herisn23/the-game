package org.roldy.scene.world.debug

import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.renderer.ChunkDebugRenderer
import org.roldy.map.WorldMap
import org.roldy.map.WorldMapDebugRenderer
import org.roldy.scene.world.chunk.WorldMapChunkManager

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