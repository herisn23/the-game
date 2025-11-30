package org.roldy

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.asset.loadAsset
import org.roldy.core.stream.ChunkManager
import org.roldy.core.stream.WorldStreamer

class StreamTest(
    camera: OrthographicCamera
) {
    val atlas = TextureAtlas(loadAsset("Trees.atlas"))
    val chunkManager = ChunkManager(512f, atlas)
    val streamer = WorldStreamer(camera, atlas, chunkManager)
    val batch = SpriteBatch()

    fun render() {
        streamer.updateVisible()
        streamer.render(batch)
    }
}