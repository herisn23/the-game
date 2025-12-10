package org.roldy.rendering.unity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.absolutePathString
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.math.abs


private const val keyWordShowEars = "[ShowEars]"
private const val keyWordFullHair = "[FullHair]"
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

private fun repackBodyParts(paths: List<PathsBodyPart>) = run {
    paths.forEach { path->
        val output = Path.of(path.copyTo)
        deleteDirectory(output.toFile())
        output.toFile().mkdirs()
    }
    paths.forEach { path ->
        val output = Path.of(path.copyTo)
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
            val repacked = atlasPath.parent.resolve("repacked/${extractionDir}")
            TexturePacker.process(
                textureSettings,
                spritesDir,
                repacked.absolutePathString(),
                atlasPath.nameWithoutExtension.normalizeAssetName()
            )
            repacked.toFile().listFiles().forEach { file ->
                copy(file.toPath(), output.resolve(file.name))
            }
        }
    }
}

private fun createAtlas(path: PathsBodyPart) {
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

        fun String.clean() = this

        val textureName = texture.name.clean()
        val atlas = createAtlas(texture, metaConfig.toFile())

        val imagePath = assetsPath.resolve(textureName)
        val atlasPath = assetsPath.resolve(textureName.replace("png", "atlas"))
        val metaPath = assetsPath.resolve("repacked/${texture.nameWithoutExtension}").apply {
            toFile().mkdirs()
        }.resolve(texture.nameWithoutExtension.normalizeAssetName() + ".meta")
        copy(texture, imagePath)
        Files.writeString(atlasPath, atlas.content)
        if (path.createMeta)
            Files.writeString(metaPath, atlas.meta)
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