package org.roldy.rendering

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.roldy.core.InputProcessorDelegate
import org.roldy.core.keybind.keybinds
import org.roldy.core.pathwalker.AsyncPathfindingProxy
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.gameplay.world.MapTilePathfinder
import org.roldy.gameplay.world.SettlementGenerator
import org.roldy.gameplay.world.generator.ProceduralMapGenerator
import org.roldy.gameplay.world.generator.RoadGenerator
import org.roldy.gameplay.world.loadBiomesConfiguration
import org.roldy.input.ObjectMoveInputProcessor
import org.roldy.input.ZoomInputProcessor
import org.roldy.rendering.g2d.Diagnostics
import org.roldy.rendering.g2d.disposable.AutoDisposableApplicationAdapter
import org.roldy.rendering.map.HexagonalTiledMapCreator
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.scene.world.WorldScene
import org.roldy.rendering.scene.world.populator.WorldMapPopulator
import org.roldy.rendering.scene.world.populator.environment.RoadsPopulator
import org.roldy.rendering.scene.world.populator.environment.SettlementPopulator

class TestApp : AutoDisposableApplicationAdapter() {
    lateinit var diagnostic: Diagnostics
    lateinit var camera: OrthographicCamera
    lateinit var viewport: Viewport
    lateinit var currentScene: WorldScene

    lateinit var zoom: ZoomInputProcessor

    override fun create() {
        diagnostic = Diagnostics().disposable()
        camera = OrthographicCamera().apply {
            zoom = 100f
            position.set(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2, 1f)
        }
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)

        ////////////////  BEGIN: WORLD SCENE INITIALIZATION ////////////
        val mapData = MapData(1L, MapSize.Debug, 256)

        val noiseData = ProceduralMapGenerator(mapData).generate()

        val (tiledMap, terrainData) = HexagonalTiledMapCreator(
            mapData,
            noiseData,
            loadBiomesConfiguration("biomes-configuration.yaml")
        ).disposable().create()


        val map = WorldMap(camera, mapData, tiledMap.disposable(), terrainData).disposable()

        val pathfinder = MapTilePathfinder(map)

        val settlements = SettlementGenerator.generate(terrainData, mapData)
        val roads = RoadGenerator(map, settlements, pathfinder).generate()

        val pathfinderProxy = AsyncPathfindingProxy(pathfinder, {
            currentScene.currentPawn.coords
        }) { path ->
            currentScene.currentPawn.pathWalking(path)
        }

        zoom = ZoomInputProcessor(keybinds, camera, 1f, 10f)
        currentScene = WorldScene(
            camera,
            map,
            InputProcessorDelegate(
                listOf(
                    zoom,
                    ObjectMoveInputProcessor(keybinds, map, camera, pathfinderProxy::findPath)
                )
            ),
            WorldMapPopulator(
                map, listOf(
                    SettlementPopulator(map, settlements),
                    RoadsPopulator(map, roads)
                )
            ).disposable()
        ).disposable()

        ////////////////  END: WORLD SCENE INITIALIZATION ////////////

        currentScene.onShow()

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        diagnostic.resize(width, height)
    }


    override fun render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        viewport.apply()

        context(Gdx.graphics.deltaTime, camera) {
            currentScene.render()
            zoom.update()
        }

        diagnostic.render()

        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            System.gc()
            Thread.sleep(100)
        }
    }
}