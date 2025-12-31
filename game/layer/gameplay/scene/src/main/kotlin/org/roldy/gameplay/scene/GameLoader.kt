package org.roldy.gameplay.scene

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.utils.Disposable
import org.roldy.core.*
import org.roldy.core.coroutines.async
import org.roldy.core.coroutines.onGPUThreadBlocking
import org.roldy.core.i18n.I18N
import org.roldy.core.i18n.Strings
import org.roldy.core.keybind.keybinds
import org.roldy.data.configuration.biome.BiomesConfiguration
import org.roldy.data.configuration.harvestable.HarvestableConfiguration
import org.roldy.data.map.MapData
import org.roldy.data.map.NoiseData
import org.roldy.data.state.GameState
import org.roldy.data.tile.MineTileData
import org.roldy.data.tile.RoadTileData
import org.roldy.data.tile.SettlementTileData
import org.roldy.data.tile.TileData
import org.roldy.gp.world.PlayerManager
import org.roldy.gp.world.generator.MineGenerator
import org.roldy.gp.world.generator.ProceduralMapGenerator
import org.roldy.gp.world.generator.RoadGenerator
import org.roldy.gp.world.generator.SettlementGenerator
import org.roldy.gp.world.input.GameSaveInputProcessor
import org.roldy.gp.world.input.MouseHandleInputProcessor
import org.roldy.gp.world.input.ZoomInputProcessor
import org.roldy.gp.world.loadBiomesConfiguration
import org.roldy.gp.world.loadHarvestableConfiguration
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.gp.world.pathfinding.calculateTileWalkCost
import org.roldy.gui.WorldGUI
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.map.Biome
import org.roldy.rendering.map.HexagonalTiledMapCreator
import org.roldy.rendering.map.MapTerrainData
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.pawn.PawnFigure
import org.roldy.rendering.screen.world.WorldScreen
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator
import org.roldy.rendering.screen.world.populator.WorldMapPopulator
import org.roldy.rendering.screen.world.populator.environment.*
import org.roldy.state.GameSaveManager
import java.io.File
import kotlin.reflect.KProperty
import kotlin.system.measureTimeMillis

class GameLoader(
    val mapData: MapData,
    val parent: AutoDisposable,
    val camera: OrthographicCamera,
    val timeManager: TimeManager,
    saveFile: File,
    val progress: (Float, I18N.Key) -> Unit,
    val finished: GameLoader.() -> Unit
) {
    val logger by logger()
    val gameSaveManager: GameSaveManager = GameSaveManager(saveFile)

    class Loader(
        val name: I18N.Key,
        val load: () -> Unit
    )

    private val loaders: MutableList<Loader> = mutableListOf()
    private val occupiedTiles: MutableList<TileData> = mutableListOf()


    val gameState = GameLoaderProperty<GameState>()
    val tiledMap = GameLoaderProperty<TiledMap>()
    val worldMap = GameLoaderProperty<WorldMap>()
    val noise = GameLoaderProperty<Map<Vector2Int, NoiseData>>()
    val terrainData = GameLoaderProperty<Map<Vector2Int, MapTerrainData>>()
    val settlements = GameLoaderProperty<List<SettlementTileData>>()
    val roads = GameLoaderProperty<List<RoadTileData>>()
    val mines = GameLoaderProperty<List<MineTileData>>()
    val biomesConfiguration = GameLoaderProperty<BiomesConfiguration>()
    val biomes = GameLoaderProperty<List<Biome>>()
    val harvestableConfiguration = GameLoaderProperty<HarvestableConfiguration>()
    val populators = GameLoaderProperty<List<WorldChunkPopulator>>()
    val underDirtTexture = GameLoaderProperty<Texture>()
    val underWaterTexture = GameLoaderProperty<Texture>()
    val screen = GameLoaderProperty<WorldScreen>()
    val gui = GameLoaderProperty<WorldGUI>()
    val tilePathFinder = GameLoaderProperty<TilePathfinder>()
    val playerManager = GameLoaderProperty<PlayerManager>()
    val persistentObjects = GameLoaderProperty<List<Layered>>()
    val playerFigure = GameLoaderProperty<PawnFigure>()

    init {

        // CONFIGURATION LOADERS
        addLoader(Strings.loading_configuration, biomesConfiguration) {
            loadBiomesConfiguration()
        }
        addLoader(Strings.loading_configuration, harvestableConfiguration) {
            loadHarvestableConfiguration()
        }

        // MAP LOADERS
        addLoader(Strings.loading_generate_map, noise) {
            ProceduralMapGenerator(mapData).generate()
        }
        addLoader(Strings.loading_generate_map, biomes) {
            biomesConfiguration.value.biomes.map {
                Biome(it, mapData.tileSize)
            }
        }

        addLoader(Strings.loading_generate_map, underDirtTexture) {
            Texture("terrain/HexUnderDirt.png")
        }
        addLoader(Strings.loading_generate_map, underWaterTexture) {
            Texture("terrain/HexUnderWater.png")
        }

        addLoader(Strings.loading_generate_map) {
            HexagonalTiledMapCreator(
                mapData,
                noise.value,
                biomes.value,
                TextureRegion(underDirtTexture.value),
                TextureRegion(underWaterTexture.value)
            ).create().also { (map, terrainData) ->
                this.tiledMap.value = map
                this.terrainData.value = terrainData
            }
        }

        addLoader(Strings.loading_generate_map, worldMap) {
            WorldMap(camera, mapData, tiledMap.value, terrainData.value)
        }

        // GENERATOR LOADERS
        addGeneratorLoader(Strings.loading_settlements, settlements) {
            SettlementGenerator(terrainData.value, mapData, occupied()).generate()
        }
        addGeneratorLoader(Strings.loading_roads, roads) {
            RoadGenerator(worldMap.value, { settlements.value }, occupied = occupied()).generate()
        }
        addGeneratorLoader(Strings.loading_mines, mines) {
            MineGenerator(
                terrainData.value,
                mapData,
                { settlements.value },
                harvestableConfiguration.value,
                occupied()
            ).generate()
        }

        // GAME STATE Loader
        addLoader(Strings.loading_game_state, gameState) {
            if (!saveFile.exists()) {
                createGameState(mapData, settlements.value, mines.value) {
                    //TODO set player initial coordinates for new game
                    worldMap.value.data.size.width / 2 x worldMap.value.data.size.height / 2
                }
            } else {
                gameSaveManager.load()
            }
        }

        // SCREEN LOADER

        addLoader(Strings.loading_gui, gui) {
            WorldGUI()
        }

        addLoader(Strings.loading_player, playerFigure) {
            PawnFigure(gameState.value.player.pawn, camera, { tile ->
                calculateTileWalkCost(screen.value, worldMap.value)(tile)
            })
        }
        addLoader(Strings.loading_player, persistentObjects) {
            listOf(
                playerFigure.value
            )
        }

        addLoader(Strings.loading_player, tilePathFinder) {
            TilePathfinder(worldMap.value) { tile, _ ->
                calculateTileWalkCost(screen.value, worldMap.value)(tile)
            }
        }

        addLoader(Strings.loading_player, playerManager) {
            PlayerManager(
                tilePathFinder.value,
                gui.value,
                gameState.value,
                camera,
                worldMap.value,
                playerFigure.value,
            )
        }

        addLoader(Strings.loading_player, populators) {
            listOf(
                SettlementPopulator(worldMap.value, gameState.value.settlements),
                RoadsPopulator(worldMap.value, roads.value),
                MountainsPopulator(worldMap.value),
                MinesPopulator(worldMap.value, gameState.value.mines),
                FoliagePopulator(worldMap.value)
            )
        }

        addLoader(Strings.loading_finalize, screen) {
            val zoom = ZoomInputProcessor(keybinds, camera, 2f, 10f)
            WorldScreen(
                gui.value,
                timeManager,
                camera,
                worldMap.value,
                WorldMapPopulator(worldMap.value, populators.value, persistentObjects.value),
                InputProcessorDelegate(
                    listOf(
                        gui.value.stage,
                        zoom,
                        MouseHandleInputProcessor(
                            keybinds,
                            worldMap.value,
                            camera,
                            playerManager.value::moveTo,
                            playerManager.value::tileFocus,
                        ),
                        GameSaveInputProcessor(keybinds, gameState.value, gameSaveManager)
                    )
                ),
                zoom::invoke
            )
        }

        load()
    }

    fun load() {
        async { onMain ->
            val size = loaders.size

            loaders.forEachIndexed { i, loader ->
                onGPUThreadBlocking {
                    progress((i + 1).toFloat() / size, loader.name)
                }
                val took = measureTimeMillis {
                    loader.load()
                }
                logger.debug { "Loader[$i] ${loader.name.key} (took $took ms)" }
            }
            onMain {
                finished()
            }
        }
    }

    private fun <A> addLoader(name: I18N.Key, load: () -> A) {
        loaders.add(Loader(name) {
            // run loader on GPU Thread due to Gdx context related references like textures loading
            onGPUThreadBlocking {
                load()
            }
        })
    }

    private fun <P : Any> addLoader(name: I18N.Key, property: GameLoaderProperty<P>, load: () -> P) {
        addLoader(name) {
            property.value = load()
        }
    }

    @JvmName("addDisposableLoader")
    private fun <P : Disposable> addLoader(name: I18N.Key, property: GameLoaderProperty<P>, load: () -> P) {
        addLoader(name) {
            property.value = load().addDisposable()
        }
    }

    @JvmName("addDisposablesLoader")
    private fun <D : Disposable, P : List<D>> addLoader(
        name: I18N.Key,
        property: GameLoaderProperty<List<D>>,
        load: () -> P
    ) {
        addLoader(name) {
            property.value = load().map { it.addDisposable() }
        }
    }

    private fun <P : TileData> addGeneratorLoader(
        name: I18N.Key,
        property: GameLoaderProperty<List<P>>,
        load: () -> List<P>
    ) {
        addLoader(name, property) {
            load().also {
                it.addOccupied()
            }
        }
    }

    private fun occupied(): (Vector2Int) -> Boolean = { coords ->
        occupiedTiles.any { tile -> tile.coords == coords }
    }

    private fun List<TileData>.addOccupied() {
        occupiedTiles.addAll(this)
    }

    private fun <A : Disposable> A.addDisposable() =
        parent.disposable(this)

}

class GameLoaderProperty<P : Any> {
    lateinit var value: P
}

operator fun <V : Any> GameLoaderProperty<V>.getValue(thisRef: Any?, property: KProperty<*>) = value