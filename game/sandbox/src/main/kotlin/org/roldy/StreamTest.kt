package org.roldy

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.core.asset.loadAsset
import org.roldy.core.stream.WorldStreamer
import org.roldy.environment.EnvironmentalObject
import org.roldy.terrain.TerrainObjectsChunkDataManager
import org.roldy.terrain.biome.Terrain

class StreamTest(
    terrainData: Map<Pair<Int, Int>, Terrain>,
    camera: OrthographicCamera
) {
    val batch = SpriteBatch()
    val pawn = PawnTest(10f, camera, batch)
    val atlas = TextureAtlas(loadAsset("Trees.atlas"))
    val chunkManager = TerrainObjectsChunkDataManager(512f, terrainData, atlas)
    val streamer = WorldStreamer(camera, chunkManager, listOf(pawn)) {
        EnvironmentalObject(atlas)
    }


    context(delta: Float)
    fun render() {
        streamer.update()
        streamer.render(batch)
    }
}