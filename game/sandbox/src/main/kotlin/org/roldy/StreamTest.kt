package org.roldy

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.asset.loadAsset
import org.roldy.core.stream.WorldStreamer
import org.roldy.core.stream.chunk.ChunkDataManager
import org.roldy.environment.EnvironmentalObject

class StreamTest(
    camera: OrthographicCamera
) {
    val batch = SpriteBatch()
    val pawn = PawnTest(10f, camera, batch)
    val atlas = TextureAtlas(loadAsset("Trees.atlas"))
    val chunkManager = ChunkDataManager(atlas= atlas)
    val streamer = WorldStreamer(camera, chunkManager, listOf(pawn, EnvironmentalObject(atlas))) {
        EnvironmentalObject(atlas)
    }


    context(delta: Float)
    fun render() {
        streamer.update()
        streamer.render(batch)
    }
}