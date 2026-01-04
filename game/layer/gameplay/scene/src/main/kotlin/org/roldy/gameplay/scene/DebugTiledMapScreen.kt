package org.roldy.gameplay.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import org.roldy.core.Vector2Int
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.asset.loadAsset
import org.roldy.core.utils.sequencer
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.data.map.NoiseData
import org.roldy.gp.world.generator.ProceduralMapGenerator
import org.roldy.gp.world.loadBiomesConfiguration
import org.roldy.rendering.g2d.Diagnostics
import org.roldy.rendering.g2d.copy
import org.roldy.rendering.g2d.disposable.AutoDisposableScreenAdapter
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.map.Biome
import org.roldy.rendering.map.HexagonalTiledMapCreator
import org.roldy.rendering.map.MiniMap
import org.roldy.rendering.map.WorldMap
import kotlin.random.Random
import kotlin.system.exitProcess

class DebugTiledMapScreen(
    val camera: OrthographicCamera
) : AutoDisposableScreenAdapter() {
    val diagnostics by disposable { Diagnostics() }
    val tilesAtlas = AtlasLoader.tiles

    val biomeConfig = loadBiomesConfiguration()
    val biomes = biomeConfig.biomes.map(::Biome)
    val underTileAtlas = AtlasLoader.underTile
    val biomeColors by lazy {
        val hex = Texture(loadAsset("HexTileHighlighter.png"))
        biomeConfig.biomes.associate {
            it.type to hex.copy(it.color)
        }.also {
            hex.dispose()
        }
    }

    // Minimap components

    override fun resize(width: Int, height: Int) {
        diagnostics.resize(width, height)
    }

    fun generateNoise(mapData: MapData) =
        ProceduralMapGenerator(mapData).generate().also {

        }

    fun generateTile(mapData: MapData, noiseData: Map<Vector2Int, NoiseData>, colors: Boolean) =
        HexagonalTiledMapCreator(
            mapData,
            noiseData,
            tilesAtlas,
            biomes,
            underTileAtlas,
            biomeColors,
            colors
        ).create()

    var seed1 = 1
    var seed2 = -1505060409

    val sequencer by sequencer { listOf(seed1, seed2) }

    var mapData = MapData(1, MapSize.Small, 256)
    var map: WorldMap? = null
    var showColor = false
    var minimap: MiniMap? = null

    init {
        gen(mapData)
        camera.position.set(map!!.viewPortWidth / 2f, map!!.viewPortHeight / 2f, 0f)
        camera.update()
    }

    fun gen(mapData: MapData) {
        val noise = generateNoise(mapData)
        val tile = generateTile(mapData, noise, showColor)
        map = WorldMap(mapData, tile.first, tile.second)
        minimap = MiniMap(map!!, camera)
    }

    override fun render(delta: Float) {
        moveCamera(delta)
        zoomCamera(delta)
        camera.update()
        map?.render(camera)

        context(delta) {
            diagnostics.render()
        }

        // Draw pre-rendered minimap
        minimap?.render()

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            exitProcess(0)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            showColor = !showColor
            gen(mapData)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            gen(MapData(sequencer.next().toLong(), MapSize.Small, 256))
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            gen(MapData(Random.nextInt().toLong(), MapSize.Small, 256))
        }
    }

    fun moveCamera(delta: Float) {
        val speed = 10000f
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.y += speed * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.y -= speed * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.x -= speed * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.x += speed * delta
        }
    }

    fun zoomCamera(delta: Float) {
        val zoomSpeed = 40f

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom += zoomSpeed * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom -= zoomSpeed * delta
        }

        // Optional: clamp zoom
        camera.zoom = camera.zoom.coerceIn(0.3f, 100f)
    }

    override fun dispose() {
        super.dispose()
    }
}