package codegen.gui

import codegen.ClassInfo
import codegen.generateKotlinClasses
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.readLines

const val pack = "package org.roldy.gui"

abstract class GenerateClassesTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputDirectory = outputDir.get().asFile

        // Clean and create output directory
        outputDirectory.deleteRecursively()
        outputDirectory.mkdirs()

        val rootDir = project.rootProject.rootDir

        val regions = rootDir.toPath().resolve("assets/GUI.atlas").readLines().let(::getRegionNames)

        val info = ClassInfo(
            "GUITextures",
            "org.roldy.gui",
            template(regions.map {
                """val $it = GUITexture("$it", atlas)""".trimIndent()
            })

        )
        generateKotlinClasses(outputDirectory, listOf(info))

        logger.lifecycle("Generated classes in: ${outputDirectory.absolutePath}")
    }


    fun template(content: List<String>) =
        """
        $pack
        import com.badlogic.gdx.graphics.g2d.TextureAtlas
        class GUITextures(
            val atlas: TextureAtlas
        ) {
            ${content.joinToString("\n")}
        }
    """.trimIndent()

    fun getRegionNames(lines:List<String>): Set<String> {
        val regionNames = mutableListOf<String>()

        for (line in lines) {
            // Region names are lines that don't start with whitespace
            // and aren't empty or texture page declarations
            if (line.isNotBlank() &&
                !line.startsWith(" ") &&
                !line.startsWith("\t") &&
                !line.contains(".png") &&
                !line.contains(".jpg")) {

                // Check if it's not a property line (size:, format:, etc.)
                if (!line.contains(":")) {
                    regionNames.add(line.trim())
                }
            }
        }

        return regionNames.toSet()
    }
}