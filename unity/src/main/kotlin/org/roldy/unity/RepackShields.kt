package org.roldy.unity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

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
            copy(texture, imagePath)
            Files.writeString(atlasPath, atlas.content)
        }

        output
    }
    repackShields(input, "assets/weapons/shield")
}

fun repackShields(paths: List<Path>, output: String) = run {
    val outputPath = Path.of(output)
    val copyTo = outputPath.toFile()
    if (!copyTo.exists()) {
        copyTo.mkdirs()
    }
    deleteDirectory(outputPath.toFile())
    Files.createDirectories(outputPath)
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
    }.flatten().toSet()
    repacked.forEach { repacked ->
        repacked.toFile().listFiles().forEach { file ->
            val target = outputPath.resolve(file.name)
            copy(file.toPath(), target)
        }
    }
}