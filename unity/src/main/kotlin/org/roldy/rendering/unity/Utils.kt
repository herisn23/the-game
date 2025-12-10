package org.roldy.rendering.unity

import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale.getDefault
import kotlin.io.path.name
import kotlin.system.exitProcess

val textureSettings = TexturePacker.Settings().apply {
    filterMin = TextureFilter.Linear  // Instead of Linear
    filterMag = TextureFilter.Linear
    premultiplyAlpha = true
    paddingX = 2
    paddingY = 2
    duplicatePadding = true
    edgePadding = true  // Add padding at atlas edges
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

fun createAtlas(texture: Path, sourceMetaFile: File): Atlas =
    runCatching {
        val metadata = loadMetaData(texture, sourceMetaFile)
        val size = metadata.first
        val parts = metadata.second
        Atlas(
            parts.map { it.name },
            createAtlasMetaData(parts),
            atlasTemplate(texture.name, parts.map { it.boundaries }, size)
        )
    }.fold(onSuccess = { it }, onFailure = {
        println("Failed to create atlas for ${texture.name}")
        throw it
    })

fun createAtlasMetaData(parts: List<SpriteData>): String = objectMapper.writeValueAsString(
    parts.map {
        SpriteMetadata(
            it.name,
            it.flags
        )
    }
)

data class SpriteData(
    val name: String,
    val boundaries: String,
    val pivo: Pair<Double, Double>,
    val flags: Map<String, Boolean>
)

data class Atlas(
    val parts: List<String>,
    val meta: String,
    val content: String
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

fun copy(from: Path, to: Path) =
    runCatching {
        println("Copying $from to $to")
        Files.copy(
            from,
            to
        )
    }.onFailure {
        it.printStackTrace()
        exitProcess(0)
    }

fun String.normalizeAssetName() =
    if (!matches(Regex(".*\\d$"))) {
        "${this}1"
    } else {
        this
    }

