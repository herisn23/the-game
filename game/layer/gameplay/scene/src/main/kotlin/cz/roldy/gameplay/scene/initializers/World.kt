package cz.roldy.gameplay.scene.initializers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import org.roldy.data.map.MapData
import org.roldy.data.map.MapSize
import org.roldy.data.tile.walkCost
import org.roldy.gameplay.world.SettlementGenerator
import org.roldy.gameplay.world.generator.ProceduralMapGenerator
import org.roldy.gameplay.world.generator.RoadGenerator
import org.roldy.gameplay.world.loadBiomesConfiguration
import org.roldy.gameplay.world.pathfinding.TilePathfinder
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.disposable.disposable
import org.roldy.rendering.map.HexagonalTiledMapCreator
import org.roldy.rendering.map.WorldMap
import org.roldy.rendering.screen.ProxyScreen
import org.roldy.rendering.screen.world.WorldScreen
import org.roldy.rendering.screen.world.populator.WorldMapPopulator
import org.roldy.rendering.screen.world.populator.environment.RoadsPopulator
import org.roldy.rendering.screen.world.populator.environment.SettlementPopulator

fun AutoDisposable.createWorldScreen(): Screen {
    val mapData = MapData(1L, MapSize.Debug, 256)
    val noiseData = ProceduralMapGenerator(mapData).generate()

    val mapCreator by disposable {
        HexagonalTiledMapCreator(
            mapData,
            noiseData,
            loadBiomesConfiguration("biomes-configuration.yaml")
        )
    }

    val (tiledMap, terrainData) = mapCreator.create()

    val camera = OrthographicCamera().apply {
        zoom = 100f
        position.set(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2, 1f)
    }

    val map by disposable {
        WorldMap(camera, mapData, tiledMap, terrainData)
    }
    lateinit var screen: WorldScreen
    val settlements = SettlementGenerator.generate(terrainData, mapData)
    val roads = RoadGenerator(map, settlements).generate()

    val populator by disposable {
        WorldMapPopulator(
            map, listOf(
                SettlementPopulator(map, settlements),
                RoadsPopulator(map, roads)
            )
        )
    }

    val pathfinder = TilePathfinder(map) { tile, _ ->
        val objectsData = screen.chunkManager.tileData(tile)
        val terrainData = listOfNotNull(map.terrainData[tile])
        val tileData = objectsData + terrainData
        tileData.walkCost()
    }

    screen = disposable(
        WorldScreen(
            camera,
            map,
            populator,
            pathfinder::findPath
        )
    )

    return ProxyScreen(screen, camera)
}