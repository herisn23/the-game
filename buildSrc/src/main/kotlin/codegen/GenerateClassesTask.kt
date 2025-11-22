package codegen

import codegen.gen.Armors
import codegen.gen.Customization
import codegen.gen.Weapons
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

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
        generateKotlinClasses(outputDirectory, Weapons.generate(rootDir))
        generateKotlinClasses(outputDirectory, Armors.generate(rootDir))
        generateKotlinClasses(outputDirectory, Customization.generate(rootDir))

        logger.lifecycle("Generated classes in: ${outputDirectory.absolutePath}")
    }

    private fun generateKotlinClasses(outputDir: File, classes: List<ClassInfo>) {
        classes.forEach { classInfo ->
            val packageDir = File(outputDir, classInfo.pckg)
            packageDir.mkdirs()
            val kotlinFile = File(packageDir, "${classInfo.name}.kt")
            kotlinFile.writeText(classInfo.source)

            logger.lifecycle("Generated: ${kotlinFile.absolutePath}")
        }

    }
}

data class ClassInfo(
    val name: String,
    val pckg: String,
    val source: String
)