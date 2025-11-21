package org.roldy.unity

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale.getDefault
import javax.imageio.ImageIO
import kotlin.io.path.absolutePathString
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.math.abs
import kotlin.system.exitProcess

val yaml = Yaml()
val objectMapper = jacksonObjectMapper()
const val sourceContext = "/Users/lukastreml/My project/"
const val spineContext = "spine"

data class Paths(
    val relativePath: String
) {
    val sourcePath = "$sourceContext/$relativePath"
    val outputPath = "$spineContext/$relativePath"
    val extractionPath = "$outputPath/extracted"
}

fun sources(vararg paths: String) =
    paths.map { Paths(it) }

fun main() {
    val paths = sources(
        "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/Armor/Basic"
    )
    paths.forEach(::createAtlas)
    extractSprites(paths)
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
        val atlas = createAtlas(texture.name, metaConfig.toFile(), image.width to image.height)
        val imagePath = assetsPath.resolve(texture.name)
        val atlasPath = assetsPath.resolve("${texture.nameWithoutExtension.lowercase()}.atlas")
        Files.copy(texture, imagePath)
        Files.writeString(atlasPath, atlas.content)
    }
}

fun extractSprites(paths: List<Paths>) {
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
                    AtlasExtractor.extractAtlas(atlas, "${path.extractionPath}/${extractionDir}")
                }
                val textures = dir
                    .let(Files::list)
                    .toList()
                    .filter { it.name.endsWith(".png") }
                textures.forEach { texture ->
                    cleanPNG(texture, texture)
                }
            }
            exitProcess(0)
        }
        fun checkPixMap(path:Path) {
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
        val parts = spriteSheet["sprites"]!!.map {
            val name = (it["name"] as String).let(::normalizeName)
            val rect = it["rect"] as Map<String, Int>
            val x = rect["x"]!!
            val y = rect["y"]!!
            val width = rect["width"]!!
            val height = rect["height"]!!
            val pivot = (it["pivot"]!! as HashMap<String, Double>).let {
                it["x"]!! to it["y"]!!
            }
            val recalcY = abs(height + y - size.second)
            SpriteData(
                name,
                atlasBoundariesTemplate(name, x, recalcY, width, height),
                pivot
            )
        }
        Atlas(
            parts.map { it.name },
            createAtlasMetaData(parts),
            atlasTemplate(textureName, parts.map { it.atlasBoundaries }, size)
        )
    }.fold(onSuccess = { it }, onFailure = {
        println("Failed to create atlas for $textureName")
        throw it
    })

data class SpriteData(
    val name: String,
    val atlasBoundaries: String,
    val pivot: Pair<Double, Double>
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
            MetaConfig(
                Vector(it.pivot)
            )
        )
    }
)

data class SpriteMetadata(
    val name: String,
    val metadata: MetaConfig
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