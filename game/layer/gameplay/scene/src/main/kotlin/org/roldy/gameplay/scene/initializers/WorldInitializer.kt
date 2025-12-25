package org.roldy.gameplay.scene.initializers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.core.*
import org.roldy.core.coroutines.DeltaProcessingLoop
import org.roldy.core.keybind.keybinds
import org.roldy.core.pathwalker.AsyncPathfindingProxy
import org.roldy.core.utils.project
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.data.state.MineState
import org.roldy.data.tile.walkCost
import org.roldy.gameplay.scene.GameTime
import org.roldy.gp.world.generator.MineGenerator
import org.roldy.gp.world.generator.ProceduralMapGenerator
import org.roldy.gp.world.generator.RoadGenerator
import org.roldy.gp.world.generator.SettlementGenerator
import org.roldy.gp.world.input.DebugInputProcessor
import org.roldy.gp.world.input.GameSaveInputProcessor
import org.roldy.gp.world.input.MouseHandleInputProcessor
import org.roldy.gp.world.input.ZoomInputProcessor
import org.roldy.gp.world.loadBiomesConfiguration
import org.roldy.gp.world.loadHarvestableConfiguration
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.gp.world.processor.RefreshingProcessor
import org.roldy.gui.WorldGUI
import org.roldy.gui.popup.data.minePopupContent
import org.roldy.rendering.g2d.Diagnostics
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

fun AutoDisposable.createWorldScreen(
    timeManager: TimeManager,
    processingLoop: DeltaProcessingLoop
): Screen {
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
        zoom = 3f
        position.set(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2, 1f)
    }

    val map by disposable {
        WorldMap(camera, mapData, tiledMap, terrainData)
    }
    lateinit var screen: WorldScreen
    val settlements = SettlementGenerator(terrainData, mapData).generate()
    val roads = RoadGenerator(map, settlements).generate()
    val mines = MineGenerator(
        terrainData,
        mapData,
        settlements,
        harvestableConfiguration
    ).generate()

    val pathfinder = TilePathfinder(map) { tile, _ ->
        val objectsData = screen.chunkManager.tileData(tile)
        val terrainData = listOfNotNull(map.terrainData[tile])
        val tileData = objectsData + terrainData
        tileData.walkCost()
    }

    val gameState = load {
        createGameState(mapData, settlements, mines) {
            map.data.size.width / 2 x map.data.size.height / 2
        }
    }

    val zoom = ZoomInputProcessor(keybinds, camera, 2f, 10f)


    var foundMine: MineState? = null
    val currentPawn: PawnFigure by disposable {
        PawnFigure(gameState.player.pawn, camera, {
            val objects = screen.chunkManager.tileData(it)
            (objects + listOfNotNull(map.terrainData[it])).walkCost()
        }) {
//            foundMine = gameState.mines.find { mine -> mine.coords == it }
//            foundMine?.let {
//                if (it.refreshing.current == it.refreshing.max) {
//                    foundMine?.refreshing?.current = 0
//                }
//            }
        }.apply {
            //initialize world position based on coords
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
                MinesPopulator(map, gameState.mines),
                FoliagePopulator(map)
            ),
            listOf(currentPawn)
        )
    }

    val gui = WorldGUI()

    fun tileFocus(coords: Vector2Int) {
        fun screen(): Vector2Int {
            val world = map.tilePosition.resolve(coords)
            camera.project(world)
            val flippedY = Gdx.graphics.height - world.y
            return world.x.toInt() x flippedY.toInt()
        }
        gui.tileTooltip.first.hide()

        val mine = gameState.mines.find { mine -> mine.coords == coords }
        mine?.let {
            gui
                .showTileInfo({ popup->
                    minePopupContent(popup, it)
                }, ::screen)
        }

    }

    screen = WorldScreen(
        gui,
        timeManager,
        camera,
        map,
        populator,
        InputProcessorDelegate(
            listOf(
                gui.stage,
                zoom,
                MouseHandleInputProcessor(
                    keybinds,
                    map,
                    camera,
                    pathfinderProxy::findPath,
                    ::tileFocus,
                ),
                GameSaveInputProcessor(keybinds, gameState),
                DebugInputProcessor(timeManager) {
                    currentPawn.coords = mapData.size.max / 2
                    currentPawn.position = map.tilePosition.resolve(currentPawn.data.coords)
                }
            )
        ),
        zoom::invoke
    ).disposable()

    val refreshingProcessor = RefreshingProcessor(gameState)
    val gameTime = GameTime(gameState.time)

    Diagnostics.addProvider {
        "Game time: ${gameTime.formattedTime}"
    }
    Diagnostics.addProvider {
        "Current mining: ${foundMine?.refreshing?.current}"
    }

    processingLoop.addConsumer(refreshingProcessor)
    processingLoop.addConsumer {
        gameTime.update()
        gameState.time = gameTime.time
    }

    return ProxyScreen(screen, camera)
}