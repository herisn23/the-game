package codegen.tiles

import codegen.ClassInfo
import codegen.generateKotlinClasses
import codegen.getRegionNames
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.readLines

const val pack = "org.roldy.rendering.tiles"

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
        val assets = listOf("Decors", "Tiles")

        assets.forEach {
            val names = rootDir.toPath().resolve("assets/tiles/${it}.atlas").readLines().let(::getRegionNames)
            val info = ClassInfo(
                it,
                pack,
                """
                package $pack
                object $it {
                    ${names.joinToString("\n"){
                    """val $it = "$it"""".trimIndent()
                    }}
                }
                """.trimIndent()
            )
            generateKotlinClasses(outputDirectory, listOf(info))
        }




        logger.lifecycle("Generated classes in: ${outputDirectory.absolutePath}")
    }

}