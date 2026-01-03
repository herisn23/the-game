package org.roldy.rendering.unity

import com.badlogic.gdx.tools.texturepacker.TexturePacker
import org.roldy.rendering.rendering.unity.terrainSettings
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.name
import kotlin.system.exitProcess

class TerrainTextureData(
    val biomeName: String,
    pathStr: String,
    val textureNames: List<String>
) {
    private val path = Path(sourceContext).resolve(pathStr)
    val sourceTextures = textureNames.map(path::resolve)

}

private val textureAssets = listOf(
    TerrainTextureData(
        "Water",
        "Assets/Game Buffs/Stylized Beach & Desert Textures/Textures",
        listOf(
            "Water_1",
            "Water_8",
            "Water_7",
            "Water_6",
            "Water_9",
            "Beach_Sand_10",
            "Beach_Sand_2"
        )
    ),
    TerrainTextureData(
        "Savanna",
        "Assets/Game Buffs/Stylized Beach & Desert Textures/Textures",
        listOf(
            "Sandy_Grass_3",
            "Sandy_Grass_6",
            "Dry_Soil_1",
            "Dry_Soil_2",
            "Dry_Soil_3"
        )
    ),
    TerrainTextureData(
        "Desert",
        "Assets/Game Buffs/Stylized Beach & Desert Textures/Textures",
        listOf(
            "Desert_Sand_1",
            "Desert_Sand_8",
            "Sand_24",
            "Sand_22"
        )
    ),
    TerrainTextureData(
        "DeepDesert",
        "Assets/Game Buffs/Stylized Beach & Desert Textures/Textures",
        listOf(
            "Rippled_Sand_1",
            "Rippled_Sand_5",
            "Rippled_Sand_10"
        )
    ),
    TerrainTextureData(
        "Forest",
        "Assets/Game Buffs/Stylized Forest Textures/Textures",
        listOf(
            "Grass_1",
            "Grass_2",
            "Grass_3",
            "Grass_4"
        )
    ),
    TerrainTextureData(
        "Jungle",
        "Assets/Game Buffs/Stylized Forest Textures/Textures",
        listOf(
            "Jungle_Floor_1",
            "Jungle_Floor_2",
            "Jungle_Floor_3",
            "Jungle_Floor_4"
        )
    ),
    TerrainTextureData(
        "Swamp",
        "Assets/Game Buffs/Stylized Forest Textures/Textures",
        listOf(
            "Mud_1",
            "Mud_2",
            "Mud_3",
            "Mud_4",
        )
    ),
    TerrainTextureData(
        "Snow",
        "Assets/Game Buffs/Stylized Arctic Textures/Textures",
        listOf(
            "Dirty_Snow_1",
            "Snow_2",
            "Snow_13"
        )
    ),
    TerrainTextureData(
        "Ice",
        "Assets/Game Buffs/Stylized Arctic Textures/Textures",
        listOf(
            "Ice_10",
            "Ice_3",
            "Ice_12"
        )
    )
)

val repackPath = Path(spineContext).resolve("tiles")
val repackSourcepath = repackPath.resolve("sources")
val assetPath = Path("assets").resolve("tiles")

fun repackTerrainTextures(generateTransitions: Boolean = true, generateInnerCorners: Boolean = false) {
    repackPath.toFile().run {
        deleteRecursively()
        mkdirs()
    }
    repackSourcepath.toFile().run {
        deleteRecursively()
        mkdirs()
    }
    assetPath.toFile().run {
        deleteRecursively()
        mkdirs()
    }
    copyTextures()

    // Generate transition tiles if enabled
    if (generateTransitions) {
        generateTransitionTiles(generateInnerCorners)
    }

    pack()
//    repackPath.toFile().run {
//        deleteRecursively()
//    }
}

private fun pack() {
    runCatching {
        textureAssets.forEach {
            TexturePacker.process(
                terrainSettings,
                repackSourcepath.resolve(it.biomeName).absolutePathString(),
                assetPath.absolutePathString(),
                "${it.biomeName}.atlas"
            )
        }
//            val atlas = TextureAtlas(Gdx.files.absolute("${repackTarget.absolutePathString()}/${it.key}.atlas"))
//            val names = atlas.regions.joinToString("\n") { it.name }
//            val namesFile = repackTarget.resolve("TileNames").toFile()
//            if (!namesFile.exists()) {
//                namesFile.writeText(names)
//            }
    }.onFailure {
        it.printStackTrace()
        exitProcess(0)
    }
}

private fun copyTextures() {
    val allowed = listOf("Albedo")
    textureAssets.forEach { data ->
        val biomeDir = repackSourcepath.resolve(data.biomeName)
        biomeDir.toFile().mkdirs()
        data.sourceTextures.forEach { file->
            runCatching {
                file.toFile().listFiles().forEach { sourceFile ->
                    if (sourceFile.extension == "png" && (allowed.any {
                            sourceFile.name.contains(it)
                        })) {
                        val targetFile = biomeDir.resolve("${file.name.replace("_", "")}.png")
                        copy(sourceFile.toPath(), targetFile)
                    }
                }
            }.onFailure {
                println(file.absolutePathString())
                it.printStackTrace()
                exitProcess(1)
            }
        }
    }
}

/**
 * Generates transition tiles for all terrain textures
 */
private fun generateTransitionTiles(generateInnerCorners: Boolean) {
    println("\n╔════════════════════════════════════════════════════════════════╗")
    println("║          GENERATING TERRAIN TRANSITION TILES                  ║")
    println("╚════════════════════════════════════════════════════════════════╝\n")

    textureAssets.forEach { biomeData ->
        val biomeDir = repackSourcepath.resolve(biomeData.biomeName)

        TransitionTileGenerator.generateTransitionsForBiome(
            biomeData = biomeData,
            sourceDir = biomeDir,
            outputDir = biomeDir,
            generateInnerCorners = generateInnerCorners
        )
    }

    println("╔════════════════════════════════════════════════════════════════╗")
    println("║          TRANSITION TILE GENERATION COMPLETE                  ║")
    println("╚════════════════════════════════════════════════════════════════╝\n")
}