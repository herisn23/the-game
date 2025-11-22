package org.roldy.unity

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.Locale.getDefault
import javax.imageio.ImageIO
import kotlin.io.path.absolutePathString
import kotlin.io.path.name
import kotlin.math.abs
import kotlin.system.exitProcess

val yaml = Yaml()
val objectMapper = jacksonObjectMapper()
const val sourceContext = "/Users/lukastreml/My project/"
const val spineContext = "spine"
const val keyWordShowEars = "[ShowEars]"
const val keyWordFullHair = "[FullHair]"

enum class Flag {
    ShowEars, ShowHair
}

data class Paths(
    val relativePath: String,
    val copyTo: String,
    val createMeta: Boolean = false
) {
    val sourcePath = "$sourceContext/$relativePath"
    val outputPath = "$spineContext/$relativePath"
    val extractionPath = "$outputPath/extracted"
}

fun main() {

    val paths = listOf(
        Paths(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Body/Basic",
            "assets/pawn/human/customization/body"
        ),
        Paths(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Eyes/Basic",
            "assets/pawn/human/customization/eyes"
        ),
        Paths(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Hair/Basic",
            "assets/pawn/human/customization/hair"
        ),
        Paths(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Mouth/Basic",
            "assets/pawn/human/customization/mouth"
        ),
        Paths(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Eyebrows/Basic",
            "assets/pawn/human/customization/eyebrows"
        ),
        Paths(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Ears/Basic",
            "assets/pawn/human/customization/ears"
        ),
        Paths(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Beard/Basic",
            "assets/pawn/human/customization/beard"
        ),
        Paths(
            "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/Armor/Basic",
            "assets/pawn/human/armor",
            true
        ),
        Paths(
            "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/Armor/Epic",
            "assets/pawn/human/armor/epic",
            true
        )
    )
    paths.forEach(::createAtlas)
    reprocess(paths)
}

fun createAtlas(path: Paths) {
    val sourcePath = Path
        .of(path.sourcePath)
        .let(Files::list)
        .toList()
    val assetsPath = Path.of(path.outputPath)
    val images = sourcePath.filter { it.name.endsWith(".png") }
    val atlasList = images.map {
        it to sourcePath.findMetaConfig(it)
    }
    if (deleteDirectory(assetsPath.toFile())) {
        println("Assets destination deleted")
    }
    if (assetsPath.toFile().mkdirs()) {
        println("Assets destination created")
    }
    atlasList.forEach { (texture, metaConfig) ->
        val image = ImageIO.read(texture.toFile())
        fun String.clean() =
            this

        val textureName = texture.name.clean()
        val atlas = createAtlas(texture.name, metaConfig.toFile(), image.width to image.height)

        val imagePath = assetsPath.resolve(textureName)
        val atlasPath = assetsPath.resolve(textureName.replace("png", "atlas"))
        val metaPath = assetsPath.resolve("repacked").apply {
            toFile().mkdirs()
        }.resolve(textureName.replace("png", "meta"))
        Files.copy(texture, imagePath)
        Files.writeString(atlasPath, atlas.content)
        if (path.createMeta)
            Files.writeString(metaPath, atlas.meta)
    }
}

fun reprocess(paths: List<Paths>) {
    Lwjgl3Application(object : ApplicationAdapter() {
        override fun create() {
            paths.forEach { path ->
                val dir = Path
                    .of(path.outputPath)
                val atlases = dir
                    .let(Files::list)
                    .toList()
                    .filter { it.name.endsWith(".atlas") }
                atlases.forEach { atlasPath ->
                    val atlas = TextureAtlas(Gdx.files.absolute(atlasPath.absolutePathString()))
                    val extractionDir = atlasPath.name.replace(".atlas", "")
                    val spritesDir = "${path.extractionPath}/${extractionDir}"
                    AtlasExtractor.extractAtlas(atlas, spritesDir)
                    val settings = TexturePacker.Settings().apply {
                        filterMin = TextureFilter.Linear  // Instead of Linear
                        filterMag = TextureFilter.Linear
                        premultiplyAlpha = true
//                        bleed = true  // Important! Extends edge pixels
//                        bleedIterations = 2  // More iterations = more bleed
                        paddingX = 2
                        paddingY = 2
                        duplicatePadding = true
                        edgePadding = true  // Add padding at atlas edges
                    }
                    val repacked = atlasPath.parent.resolve("repacked")
                    TexturePacker.process(settings, spritesDir, repacked.absolutePathString(), atlasPath.name)
//                    addCustomProperties("$output/${atlasPath.name}", customData)
                }
//                val textures = dir
//                    .let(Files::list)
//                    .toList()
//                    .filter { it.name.endsWith(".png") }
//                textures.forEach { texture ->
//                    cleanPNG(texture, texture)
//                }
            }
            paths.forEach { path->
                val repacked = Path.of(path.outputPath).resolve("repacked")
                repacked.toFile().list().forEach { file ->
                    val sourceFile = repacked.resolve(file)
                    Files.copy(
                        sourceFile,
                        Path.of(path.copyTo).run {
                            val copyTo = toFile()
                            if (!copyTo.exists()) {
                                copyTo.mkdirs()
                            }
                            resolve(sourceFile.name)
                        },
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }

            }
            exitProcess(0)
        }

        fun addCustomProperties(atlasPath: String, customData: Map<String, Map<String, String>>) {
            val atlasFile = Gdx.files.local(atlasPath)
            val lines = atlasFile.readString().lines().toMutableList()

            var i = 0
            while (i < lines.size) {
                val line = lines[i]

                // Find region names (lines that don't start with whitespace and aren't metadata)
                if (line.isNotEmpty() && !line.startsWith(" ") && !line.contains(":") && !line.endsWith(".png")) {
                    val regionName = line.trim()

                    // Add custom properties after this region
                    if (customData.containsKey(regionName)) {
                        var insertIndex = i + 1

                        // Find where to insert (after existing properties like bounds, rotate, etc.)
                        while (insertIndex < lines.size && lines[insertIndex].startsWith("  ")) {
                            insertIndex++
                        }

                        // Insert custom properties
                        customData[regionName]?.forEach { (key, value) ->
                            lines.add(insertIndex, "  $key: $value")
                            insertIndex++
                        }
                    }
                }
                i++
            }

            atlasFile.writeString(lines.joinToString("\n"), false)
        }

        val customData = mapOf(
            "left" to mapOf(
                "showEars" to "false",
                "armorType" to "helmet"
            ),
            "frontArmRight" to mapOf(
                "showEars" to "true",
                "armorType" to "sleeve"
            )
        )

        fun checkPixMap(path: Path) {
            val sourcePixmap = Pixmap(Gdx.files.absolute(path.absolutePathString()))
// Check a pixel that should be transparent
            val color = Color()
            Color.rgba8888ToColor(color, sourcePixmap.getPixel(0, 0))
            println("Alpha: ${color.a}, RGB: ${color.r}, ${color.g}, ${color.b}")
            sourcePixmap.dispose()
        }

        fun cleanPNG(inputPath: Path, outputPath: Path) {
            val pixmap = Pixmap(Gdx.files.absolute(inputPath.absolutePathString()))
            val pixels = pixmap.pixels

            // Direct byte buffer manipulation
            for (i in 0 until pixmap.width * pixmap.height) {
                val byteIndex = i * 4  // RGBA = 4 bytes per pixel

                val r = pixels.get(byteIndex).toInt() and 0xFF
                val g = pixels.get(byteIndex + 1).toInt() and 0xFF
                val b = pixels.get(byteIndex + 2).toInt() and 0xFF
                val a = pixels.get(byteIndex + 3).toInt() and 0xFF

                // If alpha is 0 or very low, set RGB to 0
                if (a < 5) {
                    pixels.put(byteIndex, 0.toByte())      // R
                    pixels.put(byteIndex + 1, 0.toByte())  // G
                    pixels.put(byteIndex + 2, 0.toByte())  // B
                    // Keep alpha as is
                }
            }

            pixels.rewind()  // Reset position

            PixmapIO.writePNG(Gdx.files.absolute(outputPath.absolutePathString()), pixmap)
            pixmap.dispose()
        }
    })
}


fun deleteDirectory(directoryToBeDeleted: File): Boolean {
    val allContents = directoryToBeDeleted.listFiles()
    if (allContents != null) {
        for (file in allContents) {
            deleteDirectory(file)
        }
    }
    return directoryToBeDeleted.delete()
}

fun List<Path>.findMetaConfig(image: Path) =
    first { path ->
        path.name.run {
            this.contains(image.name) && this.endsWith("meta")
        }
    }


fun createAtlas(textureName: String, sourceMetaFile: File, size: Pair<Int, Int>): Atlas =
    runCatching {
        val data = yaml.load<MutableMap<String, Any>>(sourceMetaFile.inputStream())
        val content = data.getContent()
        val spriteSheet = content["spriteSheet"] as Map<String, List<Map<String, Any>>>
        val flags = textureName.run {
            mapOf(
                Flag.ShowEars.name.decapitalize() to contains(keyWordShowEars),
                Flag.ShowHair.name.decapitalize() to contains(keyWordFullHair)
            )
        }
        val parts = spriteSheet["sprites"]!!.map {
            val name = (it["name"] as String).let(::normalizeName)
            val rect = it["rect"] as Map<String, Int>
            val x = rect["x"]!!
            val y = rect["y"]!!
            val width = rect["width"]!!
            val height = rect["height"]!!
            val recalcY = abs(height + y - size.second)
            SpriteData(
                name,
                atlasBoundariesTemplate(name, x, recalcY, width, height),
                flags
            )
        }
        Atlas(
            parts.map { it.name },
            createAtlasMetaData(parts),
            atlasTemplate(textureName, parts.map { it.boundaries }, size)
        )
    }.fold(onSuccess = { it }, onFailure = {
        println("Failed to create atlas for $textureName")
        throw it
    })

data class SpriteData(
    val name: String,
    val boundaries: String,
    val flags: Map<String, Boolean>
)

data class Atlas(
    val parts: List<String>,
    val meta: String,
    val content: String
)

fun createAtlasMetaData(parts: List<SpriteData>): String = objectMapper.writeValueAsString(
    parts.map {
        SpriteMetadata(
            it.name,
            it.flags
        )
    }
)

data class SpriteMetadata(
    val name: String,
    val flags: Map<String, Boolean>
)

data class Vector(
    val x: Double,
    val y: Double,
) {
    constructor(vector: Pair<Double, Double>) : this(vector.first, vector.second)
}

data class MetaConfig(
    val pivot: Vector
)


fun atlasTemplate(
    textureName: String,
    parts: List<String>,
    size: Pair<Int, Int>
) = """
$textureName
size:${size.first},${size.second}
repeat:none
filter:Linear,Linear
pma:true
${parts.joinToString("\n")}
""".trimIndent()

fun atlasBoundariesTemplate(
    name: String,
    vararg dimension: Int,
) =
    """
$name
bounds:${dimension.joinToString(",")}
""".trimIndent()

fun Boolean.toInt() = if (this) 1 else 0
fun normalizeName(name: String) =
    name.replaceFirstChar { it.lowercase(getDefault()) }
        .let(::expandAbbreviation)

fun expandAbbreviation(text: String): String {
    return when {
        text.endsWith("L") -> text.dropLast(1) + "Left"
        text.endsWith("R") -> text.dropLast(1) + "Right"
        else -> text
    }
}

fun Map<String, Any>.getContent(): Map<String, Any> =
    this["TextureImporter"] as Map<String, Any>