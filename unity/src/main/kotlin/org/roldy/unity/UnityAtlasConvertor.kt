package org.roldy.unity

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale.getDefault
import javax.imageio.ImageIO
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.math.abs

val yaml = Yaml()
val objectMapper = jacksonObjectMapper()
fun main(args: Array<String>) {
    val sourcePath = Path.of(args[0]).let(Files::list).toList()
    val assetsPath = Path.of("assets/${args[1]}")
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
        val partsPath = assetsPath.resolve("${texture.nameWithoutExtension.lowercase()}.parts")
        val metadataPath = assetsPath.resolve("${texture.nameWithoutExtension}.atlas.meta")
        Files.copy(texture, imagePath)
        Files.writeString(atlasPath, atlas.content)
        Files.writeString(partsPath, atlas.parts.joinToString("\n"))
        Files.writeString(metadataPath, atlas.meta)
    }

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
            this.contains(image.name) && this != image.name
        }
    }


fun createAtlas(textureName: String, sourceMetaFile: File, size: Pair<Int, Int>): Atlas {
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
    return Atlas(
        parts.map { it.name },
        createAtlasMetaData(parts),
        atlasTemplate(textureName, parts.map { it.atlasBoundaries }, size)
    )
}

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
    val metadata:MetaConfig
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