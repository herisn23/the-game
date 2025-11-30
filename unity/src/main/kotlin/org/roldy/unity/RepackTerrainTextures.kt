package org.roldy.unity

import com.badlogic.gdx.tools.texturepacker.TexturePacker
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
            "Beach_Sand_2",
            "Beach_Sand_9",
            "Beach_Sand_10",
            "Beach_Sand_31",
            "Beach_Sand_30",
            "Water_1",
            "Water_8",
            "Water_7",
            "Water_6",
            "Water_9"
        )
    )
)

val repackPath = Path(spineContext).resolve("terrain")
val repackSourcepath = repackPath.resolve("sources")
val assetPath = Path("assets").resolve("terrain")

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
        data.sourceTextures.forEach {
            it.toFile().listFiles().forEach { sourceFile ->
                if (sourceFile.extension == "png" && (allowed.any {
                        sourceFile.name.contains(it)
                    })) {
                    val targetFile = biomeDir.resolve("${it.name.replace("_", "")}.png")
                    copy(sourceFile.toPath(), targetFile)
                }
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