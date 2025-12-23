package codegen

import org.gradle.api.DefaultTask
import java.io.File

data class ClassInfo(
    val name: String,
    val pckg: String,
    val source: String
)

fun DefaultTask.generateKotlinClasses(outputDir: File, classes: List<ClassInfo>) {
    classes.forEach { classInfo ->
        val packageDir = File(outputDir, classInfo.pckg)
        packageDir.mkdirs()
        val kotlinFile = File(packageDir, "${classInfo.name}.kt")
        kotlinFile.writeText(classInfo.source)

        logger.lifecycle("Generated: ${kotlinFile.absolutePath}")
    }
}