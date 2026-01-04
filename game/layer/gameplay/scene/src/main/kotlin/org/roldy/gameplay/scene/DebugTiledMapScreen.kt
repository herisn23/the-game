package org.roldy.gameplay.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
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
    private val minimapCamera = OrthographicCamera()
    private var minimapFBO: FrameBuffer? = null
    private val minimapBatch by disposable { SpriteBatch() }
    private val minimapShapeRenderer by disposable { ShapeRenderer() }
    private val minimapSize = 250f
    private val minimapX = 20f
    private val minimapY = 20f
    private var mapSize = 0f

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
    var aspect = 1f

    init {
        gen(mapData)
        mapSize = MapSize.Small.size * 256f
        camera.position.set(mapSize / 2, mapSize / 2 - 9000, 0f)
        camera.update()

        // Setup minimap camera to view entire map
        minimapCamera.setToOrtho(false, mapSize, mapSize)
        minimapCamera.position.set(mapSize / 2, mapSize / 2, 0f)
        minimapCamera.update()
    }

    fun gen(mapData: MapData) {
        val noise = generateNoise(mapData)
        val tile = generateTile(mapData, noise, showColor)
        map = WorldMap(mapData, tile.first, tile.second)

        // Update map size and minimap camera when regenerating
        mapSize = mapData.size.size.toFloat()// * mapData.chunkSize.toFloat()
        minimapCamera.setToOrtho(false, mapSize, mapSize)
        minimapCamera.position.set(mapSize / 2, mapSize / 2, 0f)
        minimapCamera.update()

        // Pre-render the minimap once
        preRenderMinimap()
    }

    private fun preRenderMinimap() {
        // Dispose old FBO if exists
        minimapFBO?.dispose()
        val size = mapData.tileSize
        // Create new FBO for minimap
        val mapWidthPixels = map!!.data.size.width * size * 0.75f + size * 0.25f
        val mapHeightPixels = map!!.data.size.height * size + size * 0.5f

        // Calculate aspect ratio
        aspect = mapWidthPixels / mapHeightPixels

        val fboWidth = map!!.data.size.width * map!!.data.tileSize
        val fboHeight = (map!!.data.size.height * map!!.data.tileSize)

        minimapFBO = FrameBuffer(Pixmap.Format.RGBA8888, (4096).toInt(), (4096).toInt(), false)

        // Update minimap camera viewport to match FBO size
        minimapCamera.viewportWidth = fboWidth.toFloat()
        minimapCamera.viewportHeight = fboHeight.toFloat() * aspect
        minimapCamera.position.set(fboWidth / 2f, minimapCamera.viewportHeight / 2f, 0f)
        minimapCamera.update()

        minimapFBO?.begin()

        // Clear minimap
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Render entire map to minimap
        map?.render(minimapCamera)

        minimapFBO?.end()

        // Reset main camera
        camera.update()
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
        drawMinimap()

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

    private fun drawMinimap() {
        minimapFBO?.let { fbo ->
            // Calculate aspect ratio of the FBO
            val fboAspectRatio = aspect

            // Calculate actual draw dimensions to fit in minimap area while preserving aspect ratio
            val drawWidth: Float
            val drawHeight: Float

            if (fboAspectRatio < 1f) {
                // FBO is wider - fit to width
                drawWidth = minimapSize
                drawHeight = minimapSize / fboAspectRatio
            } else {
                // FBO is taller - fit to height
                drawHeight = minimapSize
                drawWidth = minimapSize * fboAspectRatio
            }

            // Center the minimap in the allocated space
//            val drawX = minimapX + (minimapSize - drawWidth) / 2f
//            val drawY = minimapY + (minimapSize - drawHeight) / 2f

            // Draw pre-rendered minimap texture
            minimapBatch.begin()
            minimapBatch.draw(
                fbo.colorBufferTexture,
                minimapX, minimapY,
                minimapSize, minimapSize * aspect,
                0f, 0f, 1f, 1f
            )
            minimapBatch.end()
        }

        // Draw dynamic elements (camera bounds and position)
        // Set up projection for screen coordinates
        minimapShapeRenderer.projectionMatrix.setToOrtho2D(
            0f,
            0f,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )

        // Calculate camera position in minimap coordinates
        val camXNormalized = camera.position.x / mapSize
        val camYNormalized = camera.position.y / mapSize
        val camMinimapX = minimapX + camXNormalized * minimapSize
        val camMinimapY = minimapY + camYNormalized * minimapSize

        // Calculate viewport size in minimap
        val viewWidthNormalized = (camera.viewportWidth * camera.zoom) / mapSize
        val viewHeightNormalized = (camera.viewportHeight * camera.zoom) / mapSize
        val viewMinimapWidth = viewWidthNormalized * minimapSize
        val viewMinimapHeight = viewHeightNormalized * minimapSize

        // Draw camera view bounds
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        minimapShapeRenderer.color = Color.YELLOW
        minimapShapeRenderer.rect(
            camMinimapX - viewMinimapWidth / 2,
            camMinimapY - viewMinimapHeight / 2,
            viewMinimapWidth,
            viewMinimapHeight
        )
        minimapShapeRenderer.end()

        // Draw camera center point
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        minimapShapeRenderer.color = Color.RED
        minimapShapeRenderer.circle(camMinimapX, camMinimapY, 3f)
        minimapShapeRenderer.end()

        // Draw border around minimap
        minimapShapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        minimapShapeRenderer.color = Color.WHITE
        minimapShapeRenderer.rect(minimapX, minimapY, minimapSize, minimapSize * aspect)
        minimapShapeRenderer.end()
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
        minimapFBO?.dispose()
    }
}