package org.roldy.rendering.unity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.name
data class PathsWeapons(
    val input: Path,
    val atlasName: String,
    val output: String,
)
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
                copy(
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
        PathsWeapons(
            output, setting.atlasName, "assets/${setting.output}"
        )
    }
    repackWeapons(input)
}

fun repackWeapons(paths: List<PathsWeapons>) = run {
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
}