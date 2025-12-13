package org.roldy.gameplay.scene.initializers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.core.InputProcessorDelegate
import org.roldy.core.TimeManager
import org.roldy.core.div
import org.roldy.core.keybind.keybinds
import org.roldy.core.pathwalker.AsyncPathfindingProxy
import org.roldy.core.x
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.data.state.GameState
import org.roldy.data.tile.walkCost
import org.roldy.gameplay.world.generator.MineGenerator
import org.roldy.gameplay.world.generator.ProceduralMapGenerator
import org.roldy.gameplay.world.generator.RoadGenerator
import org.roldy.gameplay.world.generator.SettlementGenerator
import org.roldy.gameplay.world.input.DebugInputProcessor
import org.roldy.gameplay.world.input.GameSaveInputProcessor
import org.roldy.gameplay.world.input.ObjectMoveInputProcessor
import org.roldy.gameplay.world.input.ZoomInputProcessor
import org.roldy.gameplay.world.loadBiomesConfiguration
import org.roldy.gameplay.world.loadHarvestableConfiguration
import org.roldy.gameplay.world.pathfinding.TilePathfinder
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.map.HexagonalTiledMapCreator
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.pawn.PawnFigure
import org.roldy.rendering.screen.ProxyScreen
import org.roldy.rendering.screen.world.WorldScreen
import org.roldy.rendering.screen.world.populator.WorldMapPopulator
import org.roldy.rendering.screen.world.populator.environment.*
import org.roldy.state.load

fun AutoDisposable.createWorldScreen(): Screen {
    val mapData = MapData(1L, MapSize.Small, 256)
    val noiseData = ProceduralMapGenerator(mapData).generate()

    val biomeConfiguration = loadBiomesConfiguration()
    val harvestableConfiguration = loadHarvestableConfiguration()

    val mapCreator by disposable {
        HexagonalTiledMapCreator(
            mapData,
            noiseData,
            biomeConfiguration
        )
    }

    val (tiledMap, terrainData) = mapCreator.create()

    val camera = OrthographicCamera().apply {
        zoom = 20f
        position.set(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2, 1f)
    }

    val map by disposable {
        WorldMap(camera, mapData, tiledMap, terrainData)
    }
    lateinit var screen: WorldScreen
    val settlements = SettlementGenerator(terrainData, mapData).generate()
    val roads = RoadGenerator(map, settlements).generate()
    val mines = MineGenerator(terrainData, mapData, settlements, harvestableConfiguration).generate()

    val pathfinder = TilePathfinder(map) { tile, _ ->
        val objectsData = screen.chunkManager.tileData(tile)
        val terrainData = listOfNotNull(map.terrainData[tile])
        val tileData = objectsData + terrainData
        tileData.walkCost()
    }

    val gameState = load {
        createGameState(settlements, mines) {
            val center = map.data.size.width / 2 x map.data.size.height / 2
            center
        }
    }

    val zoom = ZoomInputProcessor(keybinds, camera, 1f, 100f)

    val currentPawn: PawnFigure by disposable {
        PawnFigure(gameState.player.pawn, camera) {
            val objects = screen.chunkManager.tileData(it)
            (objects + listOfNotNull(map.terrainData[it])).walkCost()
        }.apply {
            position = map.tilePosition.resolve(data.coords)
        }
    }

    val pathfinderProxy = AsyncPathfindingProxy(pathfinder::findPath, currentPawn::coords) { path ->
        currentPawn.pathWalking(path)
    }

    val populator by disposable {
        WorldMapPopulator(
            map, listOf(
                SettlementPopulator(map, gameState.settlements),
                RoadsPopulator(map, roads),
                MountainsPopulator(map),
                MinesPopulator(map, gameState.mines + gameState.settlements.flatMap { it.mines }),
                FoliagePopulator(map)
            ),
            listOf(currentPawn)
        )
    }
    val timeManager = TimeManager()
    screen = disposable(
        WorldScreen(
            camera,
            map,
            populator,
            InputProcessorDelegate(
                listOf(
                    zoom,
                    ObjectMoveInputProcessor(keybinds, map, camera, pathfinderProxy::findPath),
                    GameSaveInputProcessor(keybinds, gameState),
                    DebugInputProcessor(timeManager) {
                        currentPawn.coords = mapData.size.max / 2
                        currentPawn.position = map.tilePosition.resolve(currentPawn.data.coords)
                    }
                )
            ),
            zoom::invoke
        )
    )

    return ProxyScreen(timeManager, screen, camera)
}