package org.roldy.unity

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
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

data class PathsBodyPart(
    val relativePath: String,
    val copyTo: String,
    val createMeta: Boolean = false
) {
    val sourcePath = "$sourceContext/$relativePath"
    val outputPath = "$spineContext/$relativePath"
    val extractionPath = "$outputPath/extracted"
}

data class PathsWeapons(
    val input: Path,
    val atlasName: String,
    val output: String,
)

fun main() {
//    repackWeapons()
    repackShields()
//    repackBodyParts()
}

data class ShieldPaths(
    val source: String,
    val outputPath: String
)

fun repackShields() {

    val sources = listOf(
        "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/Shield/Epic",
        "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/Shield/Basic"
    )
    val input = sources.map { source ->
        val sourcePath = Path.of("$sourceContext$source")
        val output = Path.of("$spineContext/$source")
        if (deleteDirectory(output.toFile())) {
            println("Deleted $output")
        }
        if (output.toFile().mkdirs()) {
            println("Assets destination created")
        }
        val sources = sourcePath
            .let(Files::list)
            .toList()
        val files = sourcePath.toFile().listFiles()
        val images = files.filter { it.name.endsWith(".png") }
        val atlasList = images.map {
            it.toPath() to sources.findMetaConfig(it.toPath())
        }
        atlasList.forEach { (texture, metaConfig) ->
            val textureName = texture.name
            val atlas = createAtlas(texture, metaConfig.toFile())
            val imagePath = output.resolve(textureName)
            val atlasPath = output.resolve(textureName.replace("png", "atlas"))
            Files.copy(texture, imagePath)
            Files.writeString(atlasPath, atlas.content)
        }

        output
    }
    repackShields(input, "assets/weapons/shield")
}

fun repackWeapons() {
    data class Setting(
        val sources: List<String>,
        val output: String,
        val atlasName: String
    )

    val settings = listOf(
        Setting(
            listOf(
                "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/MeleeWeapon1H/Basic/Wand",
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon1H/Epic/Wand"
            ),
            "weapons/wand", "Wand"
        ),
        Setting(
            listOf(
                "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/MeleeWeapon1H/Basic/Lance",
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon1H/Epic/Lance",
            ),
            "weapons/lance", "Lance"
        ),
        Setting(
            listOf(
                "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/MeleeWeapon1H/Basic/Hammer",
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon1H/Epic/Hammer",
                "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/MeleeWeapon2H/Basic/Hammer",
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon2H/Epic/Hammer",
            ),
            "weapons/hammer", "Hammer"
        ),
        Setting(
            listOf(
                "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/MeleeWeapon1H/Basic/Dagger",
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon1H/Epic/Dagger"
            ),
            "weapons/dagger", "Dagger"
        ),
        Setting(
            listOf(
                "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/MeleeWeapon1H/Basic/Axe",
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon1H/Epic/Axe",
                "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/MeleeWeapon2H/Basic/Axe",
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon2H/Epic/Axe"
            ),
            "weapons/axe", "Axe"
        ),
        Setting(
            listOf(
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon2H/Epic/Staff"
            ),
            "weapons/staff", "Staff"
        ),
        Setting(
            listOf(
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon2H/Epic/Sword",
                "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/MeleeWeapon1H/Epic/Sword",
                "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/MeleeWeapon1H/Basic/Sword",
                "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/MeleeWeapon2H/Basic/Sword"
            ),
            "weapons/sword", "Sword"
        )
    )
    val input = settings.map { setting ->
        val sourcePaths = setting.sources.map { s ->
            Path.of("$sourceContext/$s")
        }
        val output = Path.of("$spineContext/Assets/${setting.output}")
        if (deleteDirectory(output.toFile())) {
            println("Deleted $output")
        }
        sourcePaths.forEach { source ->
            source.toFile().listFiles().forEach { file ->
                val sourceFile = file.toPath()
                Files.copy(
                    sourceFile,
                    output.run {
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
        PathsWeapons(
            output, setting.atlasName, "assets/${setting.output}"
        )
    }
    repackWeapons(input)
}

fun repackBodyParts() {

    val paths = listOf(
        PathsBodyPart(
            "Assets/HeroEditor4D/Common/Sprites/Equipment/Armor/Underwear",
            "assets/pawn/human/customization/underwear"
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/Makeup/Basic",
            "assets/pawn/human/customization/makeup"
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Body/Basic",
            "assets/pawn/human/customization/body"
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Eyes/Basic",
            "assets/pawn/human/customization/eyes"
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Hair/Basic",
            "assets/pawn/human/customization/hair"
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Mouth/Basic",
            "assets/pawn/human/customization/mouth"
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Eyebrows/Basic",
            "assets/pawn/human/customization/eyebrows"
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Ears/Basic",
            "assets/pawn/human/customization/ears"
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/Common/Sprites/BodyParts/Beard/Basic",
            "assets/pawn/human/customization/beard"
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/FantasyHeroes/Sprites/Equipment/Armor/Basic",
            "assets/pawn/human/armor",
            true
        ),
        PathsBodyPart(
            "Assets/HeroEditor4D/Extensions/EpicHeroes/Sprites/Equipment/Armor/Epic",
            "assets/pawn/human/armor",
            true
        )
    )
    paths.forEach(::createAtlas)
    repackBodyParts(paths)
}

fun createAtlas(path: PathsBodyPart) {
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

        fun String.clean() =
            this

        val textureName = texture.name.clean()
        val atlas = createAtlas(texture, metaConfig.toFile())

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

val textureSettings = TexturePacker.Settings().apply {
    filterMin = TextureFilter.Linear  // Instead of Linear
    filterMag = TextureFilter.Linear
    premultiplyAlpha = true
    paddingX = 2
    paddingY = 2
    duplicatePadding = true
    edgePadding = true  // Add padding at atlas edges
}

fun repackShields(paths: List<Path>, output: String) {
    Lwjgl3Application(object : ApplicationAdapter() {
        override fun create() {
            val outputPath = Path.of(output)
            val copyTo = outputPath.toFile()
            if (!copyTo.exists()) {
                copyTo.mkdirs()
            }
            deleteDirectory(outputPath.toFile())
            val repacked = paths.map { path ->
                val extractionPath = path.resolve("extracted")
                val atlases = path
                    .let(Files::list)
                    .toList()
                    .filter { it.name.endsWith(".atlas") }
                atlases.map { atlasPath ->
                    println("start repacking $atlasPath")
                    val atlas = TextureAtlas(Gdx.files.absolute(atlasPath.absolutePathString()))
                    val extractionDir = atlasPath.name.replace(".atlas", "")
                    val spritesDir = "${extractionPath}/${extractionDir}"
                    AtlasExtractor.extractAtlas(atlas, spritesDir)
                    val repacked = atlasPath.parent.resolve("repacked")
                    TexturePacker.process(
                        textureSettings,
                        spritesDir,
                        repacked.absolutePathString(),
                        atlasPath.name
                    )
                    repacked
                }
            }.flatten()
            repacked.forEach { repacked ->
                repacked.toFile().listFiles().forEach { file ->
                    val target = outputPath.resolve(file.name)
                    println("Copy $file to $target")
                    Files.createDirectories(target.parent)
                    Files.copy(file.toPath(), target)
                }
            }
            exitProcess(0)
        }
    })
}

fun repackWeapons(paths: List<PathsWeapons>) {
    Lwjgl3Application(object : ApplicationAdapter() {
        override fun create() {

            paths.forEach { paths ->
                TexturePacker.process(
                    textureSettings,
                    paths.input.absolutePathString(),
                    paths.output,
                    "${paths.atlasName}.atlas"
                )
                val atlas = TextureAtlas(Gdx.files.absolute("${paths.output}/${paths.atlasName}.atlas"))
                val names = atlas.regions.joinToString("\n") { it.name }
                val namesFile = Path.of(paths.output).resolve("${paths.atlasName}.names").toFile()
                namesFile.writeText(names)
            }
            exitProcess(0)
        }
    })
}

fun repackBodyParts(paths: List<PathsBodyPart>) {
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
                    println("start repacking $atlasPath")
                    val atlas = TextureAtlas(Gdx.files.absolute(atlasPath.absolutePathString()))
                    val extractionDir = atlasPath.name.replace(".atlas", "")
                    val spritesDir = "${path.extractionPath}/${extractionDir}"
                    AtlasExtractor.extractAtlas(atlas, spritesDir)
                    val repacked = atlasPath.parent.resolve("repacked")
                    TexturePacker.process(
                        textureSettings,
                        spritesDir,
                        repacked.absolutePathString(),
                        atlasPath.name
                    )
                }
            }
            paths.forEach { path ->
                val output = Path.of(path.copyTo)
                deleteDirectory(output.toFile())
                val repacked = Path.of(path.outputPath).resolve("repacked")
                repacked.toFile().list().forEach { file ->
                    val sourceFile = repacked.resolve(file)
                    Files.copy(
                        sourceFile,
                        output.run {
                            val copyTo = toFile()
                            if (!copyTo.exists()) {
                                copyTo.mkdirs()
                            }
                            resolve(sourceFile.name)
                        }
                    )
                }

            }
            exitProcess(0)
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


fun loadMetaData(texture: Path, sourceMetaFile: File) =
    run {
        val image = ImageIO.read(texture.toFile())
        val size = image.width to image.height
        val data = yaml.load<MutableMap<String, Any>>(sourceMetaFile.inputStream())
        val content = data.getContent()
        val spriteSheet = content["spriteSheet"] as Map<String, List<Map<String, Any>>>
        val flags = texture.name.run {
            mapOf(
                Flag.ShowEars.name.decapitalize() to contains(keyWordShowEars),
                Flag.ShowHair.name.decapitalize() to contains(keyWordFullHair)
            )
        }
        size to spriteSheet["sprites"]!!.map {
            val name = (it["name"] as String).let(::normalizeName)
            val rect = it["rect"] as Map<String, Int>
            val x = rect["x"]!!
            val y = rect["y"]!!
            val width = rect["width"]!!
            val height = rect["height"]!!
            val recalcY = abs(height + y - size.second)
            val pivot = (it["pivot"]!! as HashMap<String, Double>).let {
                it["x"]!! to it["y"]!!
            }
            SpriteData(
                name,
                atlasBoundariesTemplate(name, x, recalcY, width, height),
                pivot,
                flags
            )
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