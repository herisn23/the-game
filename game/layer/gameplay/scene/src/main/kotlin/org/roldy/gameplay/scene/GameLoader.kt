package org.roldy.gameplay.scene

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.utils.Disposable
import org.roldy.core.InputProcessorDelegate
import org.roldy.core.TimeManager
import org.roldy.core.Vector2Int
import org.roldy.core.asset.AtlasLoader
import org.roldy.core.asset.loadAsset
import org.roldy.core.coroutines.DeltaProcessingLoop
import org.roldy.core.coroutines.async
import org.roldy.core.coroutines.onGPUThreadBlocking
import org.roldy.core.i18n.I18N
import org.roldy.core.i18n.Strings
import org.roldy.core.keybind.keybinds
import org.roldy.core.logger
import org.roldy.data.configuration.biome.BiomeType
import org.roldy.data.configuration.biome.BiomesConfiguration
import org.roldy.data.configuration.harvestable.HarvestableConfiguration
import org.roldy.data.map.Biome
import org.roldy.data.map.MapData
import org.roldy.data.map.NoiseData
import org.roldy.data.state.GameState
import org.roldy.data.state.HeroState
import org.roldy.data.tile.*
import org.roldy.gp.world.TileFocusManager
import org.roldy.gp.world.generator.*
import org.roldy.gp.world.input.GameSaveInputProcessor
import org.roldy.gp.world.input.MouseHandleInputProcessor
import org.roldy.gp.world.input.ZoomInputProcessor
import org.roldy.gp.world.loadBiomesConfiguration
import org.roldy.gp.world.loadHarvestableConfiguration
import org.roldy.gp.world.manager.player.PlayerManager
import org.roldy.gp.world.pathfinding.TilePathfinder
import org.roldy.gp.world.pathfinding.calculateTileWalkCost
import org.roldy.gp.world.processor.HarvestableRefreshingProcessor
import org.roldy.gui.WorldGUI
import org.roldy.rendering.g2d.Layered
import org.roldy.rendering.g2d.copy
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.map.HexagonalTiledMapCreator
import org.roldy.rendering.map.MapTerrainData
import org.roldy.rendering.map.MiniMap
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.world.WorldScreen
import org.roldy.rendering.screen.world.populator.WorldChunkPopulator
import org.roldy.rendering.screen.world.populator.WorldMapPopulator
import org.roldy.rendering.screen.world.populator.environment.*
import org.roldy.state.GameSaveManager
import java.io.File
import kotlin.math.round
import kotlin.reflect.KProperty
import kotlin.system.measureTimeMillis

fun AutoDisposable.gameLoader(
    saveFile: File,
    camera: OrthographicCamera,
    heroStateForNewGame: () -> HeroState,
    mapData: () -> MapData,
    progress: (Float, I18N.Key) -> Unit,
    finished: GameLoader.() -> Unit
): GameLoader {
    return if (saveFile.exists()) {
        GameLoader(
            this,
            camera,
            saveFile,
            progress,
            finished,
        )
    } else {
        GameLoader(
            heroStateForNewGame(),
            mapData(),
            this,
            camera,
            saveFile,
            progress,
            finished,
        )
    }.apply {
        start()
    }
}

class GameLoader {
    val parent: AutoDisposable
    val camera: OrthographicCamera
    val heroStateForNewGame: HeroState?
    val newMapData: MapData?
    val progress: (Float, I18N.Key) -> Unit
    val finished: GameLoader.() -> Unit
    var newGame: Boolean
    val gameSaveManager: GameSaveManager

    /**
     * Constructor for loading game
     */
    constructor(
        parent: AutoDisposable,
        camera: OrthographicCamera,
        saveFile: File,
        progress: (Float, I18N.Key) -> Unit,
        finished: GameLoader.() -> Unit
    ) {
        this.parent = parent
        this.camera = camera
        this.gameSaveManager = GameSaveManager(saveFile)
        this.progress = progress
        this.finished = finished
        this.heroStateForNewGame = null
        this.newMapData = null
        this.newGame = false
    }

    constructor(
        heroStateForNewGame: HeroState,
        mapData: MapData,
        parent: AutoDisposable,
        camera: OrthographicCamera,
        saveFile: File,
        progress: (Float, I18N.Key) -> Unit,
        finished: GameLoader.() -> Unit
    ) {
        this.parent = parent
        this.camera = camera
        this.gameSaveManager = GameSaveManager(saveFile)
        this.progress = progress
        this.finished = finished
        this.heroStateForNewGame = heroStateForNewGame
        this.newMapData = mapData
        this.newGame = true
    }

    val debugMode = false
    val maxZoom = if (debugMode) 100f else 100f

    val logger by logger()

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
    val harvestable = GameLoaderProperty<List<HarvestableTileData>>()
    val mountains = GameLoaderProperty<List<MountainTileData>>()

    val biomesConfiguration = GameLoaderProperty<BiomesConfiguration>()
    val biomes = GameLoaderProperty<List<Biome>>()
    val harvestableConfiguration = GameLoaderProperty<HarvestableConfiguration>()
    val populators = GameLoaderProperty<List<WorldChunkPopulator>>()
    val screen = GameLoaderProperty<WorldScreen>()
    val gui = GameLoaderProperty<WorldGUI>()
    val tilePathFinder = GameLoaderProperty<TilePathfinder>()
    val playerManager = GameLoaderProperty<PlayerManager>()
    val persistentObjects = GameLoaderProperty<MutableList<Layered>>()
    val timeManager = GameLoaderProperty<TimeManager>()
    val processingLoop = GameLoaderProperty<DeltaProcessingLoop>()
    val gameTime = GameLoaderProperty<GameTime>()
    val tileFocusManager = GameLoaderProperty<TileFocusManager>()
    val mapData = GameLoaderProperty<MapData>()
    val minimap = GameLoaderProperty<MiniMap>()

    // ATLASES
    val tilesAtlas = GameLoaderProperty<TextureAtlas>()
    val environmentAtlas = GameLoaderProperty<TextureAtlas>()
    val roadsAtlas = GameLoaderProperty<TextureAtlas>()
    val craftingIconAtlas = GameLoaderProperty<TextureAtlas>()
    val outlines = GameLoaderProperty<TextureAtlas>()
    val biomeColors = GameLoaderProperty<Map<BiomeType, Texture>>()

    private fun addLoaders() {

        // CONFIGURATION LOADERS

        addLoader(Strings.loading_configuration) {
            if (!newGame) {
                gameState.value = gameSaveManager.load()
                mapData.value = gameState.value.mapData
                newGame = false
            } else {
                mapData.value = requireNotNull(newMapData) {
                    "New game need a mapData"
                }
            }
        }

        addLoader(Strings.loading_configuration, biomesConfiguration) {
            loadBiomesConfiguration().apply {
                biomes.forEach {
                    logger.debug { "Biome walk-cost: ${it.type} = ${it.walkCost}" }
                }
            }
        }
        addLoader(Strings.loading_configuration, harvestableConfiguration) {
            loadHarvestableConfiguration()
        }

        addLoader(Strings.loading_configuration, timeManager) {
            TimeManager()
        }

        addLoader(Strings.loading_configuration, processingLoop) {
            DeltaProcessingLoop(timeManager.value)
        }

        // Loading atlasses
        addLoader(Strings.loading_textures, tilesAtlas) {
            AtlasLoader.tiles
        }
        addLoader(Strings.loading_textures, environmentAtlas) {
            AtlasLoader.tileEnvironment
        }
        addLoader(Strings.loading_textures, roadsAtlas) {
            AtlasLoader.roads
        }
        addLoader(Strings.loading_textures, craftingIconAtlas) {
            AtlasLoader.craftingIcons
        }
        addLoader(Strings.loading_textures, outlines) {
            AtlasLoader.hexOutline
        }

        // MAP LOADERS
        addLoader(Strings.loading_generate_map, noise) {
            ProceduralMapGenerator(mapData.value).generate()
        }

        addLoader(Strings.loading_generate_map, biomes) {
            biomesConfiguration.value.biomes.map {
                Biome(it)
            }
        }

        addLoader(Strings.loading_generate_map, biomeColors) {
            val hex = Texture(loadAsset("tiles/EmptyHex.png"))
            biomesConfiguration.value.biomes.associate {
                it.type to hex.copy(it.color)
            }.also {
                hex.dispose()
            }
        }

        addLoader(Strings.loading_generate_map) {
            HexagonalTiledMapCreator(
                mapData.value,
                noise.value,
                tilesAtlas.value,
                biomes.value,
                biomeColors.value,
                debugMode
            ).create().also { (map, terrainData) ->
                this.tiledMap.value = map
                this.terrainData.value = terrainData
            }.also {
                val total = this.terrainData.value.values.size
                val biomes = this.terrainData.value.values.groupBy { it.terrain.biome.data.type }
                val biomesDist = biomes.map { (key, vals) ->
                    val proportion = round((vals.size.toFloat() / total.toFloat()) * 100)
                    "$key: ${proportion}% (${vals.size})"
                }
                logger.debug {
                    """
                        
                        Biomes distribution:
                            ${biomesDist.joinToString("\n")}
                    """.trimIndent()
                }
            }
        }

        addLoader(Strings.loading_generate_map, worldMap) {
            WorldMap(mapData.value, tiledMap.value, outlines.value, terrainData.value, debugMode)
        }

        // generators for new game
        addGeneratorLoader(Strings.loading_settlements, settlements, { newGame }) {
            SettlementGenerator(terrainData.value, mapData.value, occupied()).generate()
        }
        addGeneratorLoader(Strings.loading_mines, harvestable, { newGame }) {
            HarvestableGenerator(
                terrainData.value,
                mapData.value,
                { settlements.value },
                harvestableConfiguration.value,
                occupied()
            ).generate()
        }

        // GAME STATE Loader
        addLoader(Strings.loading_game_state) {
            if (newGame && heroStateForNewGame != null) {
                gameState.value =
                    createGameState(mapData.value, settlements.value, harvestable.value, heroStateForNewGame).apply {
                        heroStateForNewGame.setSuitableSpot(this, worldMap.value)
                    }
            }
        }
        // generators based on game state
        addGeneratorLoader(Strings.loading_roads, roads) {
            RoadGenerator(worldMap.value, gameState.value.settlements, occupied = occupied()).generate()
        }
        addGeneratorLoader(Strings.loading_mountains, mountains) {
            MountainsGenerator(worldMap.value, biomes.value, occupied()).generate()
        }

        addLoader(Strings.loading_game_state, gameTime) {
            GameTime(gameState.value.time)
        }

        // GUI Loader
        addLoader(Strings.loading_gui, gui) {
            WorldGUI(craftingIconAtlas.value) {
                gameSaveManager.save(gameState.value)
            }
        }

        // Player data loader
        addLoader(Strings.loading_finalize, tilePathFinder) {
            TilePathfinder(worldMap.value) { tile, _ ->
                calculateTileWalkCost(screen.value, worldMap.value)(tile)
            }
        }

        addLoader(Strings.loading_finalize, persistentObjects) {
            mutableListOf()
        }
        addLoader(Strings.loading_finalize, populators) {
            if (!debugMode) {
                listOf(
                    SettlementPopulator(
                        worldMap.value,
                        environmentAtlas.value,
                        outlines.value,
                        gameState.value.settlements
                    ),
                    RoadsPopulator(worldMap.value, roadsAtlas.value, roads.value),
                    MountainsPopulator(worldMap.value, mountains.value, tilesAtlas.value),
                    HarvestablePopulator(
                        worldMap.value,
                        gameState.value.mines,
                        environmentAtlas.value,
                        tilesAtlas.value,
                        craftingIconAtlas.value
                    ),
                    FoliagePopulator(worldMap.value)
                )
            } else {
                emptyList()
            }
        }
        addLoader(Strings.loading_finalize, tileFocusManager) {
            TileFocusManager(
                gui.value,
                gameState.value,
                camera,
                worldMap.value
            )
        }

        addLoader(Strings.loading_finalize, minimap) {
            MiniMap(worldMap.value, camera)
        }

        addLoader(Strings.loading_finalize, screen) {
            val zoom = ZoomInputProcessor(keybinds, camera, 1f, maxZoom)
            WorldScreen(
                gui.value,
                minimap.value,
                timeManager.value,
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
                            {
                                playerManager.value.move(it)
                            },
                            tileFocusManager.value::focusTile,
                        ),
                        GameSaveInputProcessor(keybinds, gameState.value, gameSaveManager)
                    )
                ),
                zoom::invoke,
                {
                    playerManager.value.currentPosition
                }
            )
        }
        // Add processors
        addLoader(Strings.loading_finalize, playerManager) {
            PlayerManager(
                tilePathFinder.value,
                gui.value,
                gameState.value,
                screen.value,
                persistentObjects.value
            )
        }
        addLoader(Strings.loading_finalize) {
            with(processingLoop.value) {
                addConsumer(playerManager.value)
                addConsumer(HarvestableRefreshingProcessor(gameState.value))
                addConsumer {
                    gameTime.value.update()
                    gameState.value.time = gameTime.value.time
                }
            }
        }
    }

    fun start() {
        addLoaders()
        load()
    }

    private fun load() {
        async { onMain ->
            val size = loaders.size

            val total = measureTimeMillis {
                loaders.forEachIndexed { i, loader ->
                    onGPUThreadBlocking {
                        progress((i + 1).toFloat() / size, loader.name)
                    }
                    val took = measureTimeMillis {
                        loader.load()
                    }
                    logger.debug { "Loader[$i] ${loader.name.key} (took $took ms)" }
                }
            }
            logger.debug { "Loaded in $total ms" }
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

    @JvmName("addMapDisposablesLoader")
    private fun <V, D : Disposable, P : Map<V, D>> addLoader(
        name: I18N.Key,
        property: GameLoaderProperty<Map<V, D>>,
        load: () -> P
    ) {
        addLoader(name) {
            property.value = load().map { (k, v) -> k to v.addDisposable() }.toMap()
        }
    }

    private fun <P : TileData> addGeneratorLoader(
        name: I18N.Key,
        property: GameLoaderProperty<List<P>>,
        enabled: () -> Boolean = { true },
        load: () -> List<P>
    ) {
        if (enabled())
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