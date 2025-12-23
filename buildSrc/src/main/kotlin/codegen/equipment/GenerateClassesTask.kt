package codegen.equipment

import codegen.equipment.gen.Armors
import codegen.equipment.gen.Customization
import codegen.equipment.gen.Shield
import codegen.equipment.gen.Weapons
import codegen.generateKotlinClasses
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
        generateKotlinClasses(outputDirectory, Shield.generate(rootDir))

        logger.lifecycle("Generated classes in: ${outputDirectory.absolutePath}")
    }
}

