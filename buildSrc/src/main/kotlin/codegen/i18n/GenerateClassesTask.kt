package codegen.i18n

import codegen.ClassInfo
import codegen.generateKotlinClasses
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.util.Properties
import kotlin.io.path.inputStream
const val pack = "package org.roldy.core.i18n"
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

        val i18n = rootDir.toPath().resolve("i18n")

        val props = i18n.resolve("i18n.properties").inputStream().let {
            Properties().apply {
                load(it)
            }
        }

        val info = ClassInfo(
            "TextKeys",
            "org.roldy.core.i18n",
            templateKeys(props.keys.map {
                """val $it = I18N.Key("$it")""".trimIndent()
            })

        )

        val langs = i18n.toFile().listFiles().map {
            val s = it.nameWithoutExtension.split("_")
            if (s.size == 1) {
                "en"
            } else {
                s[1]
            }
        }

        val languages = ClassInfo(
            "Languages",
            "org.roldy.core.i18n",
            templateLanguages(langs)

        )

        generateKotlinClasses(outputDirectory, listOf(info, languages))

        logger.lifecycle("Generated classes in: ${outputDirectory.absolutePath}")
    }




    fun templateKeys(keys: List<String>) =
        """
        $pack
        
        object TextKeys {
            ${keys.joinToString("\n")}
        }
    """.trimIndent()

    fun templateLanguages(keys: List<String>) =
        """
        $pack
        
        import java.util.Locale
        
        object Languages {
            ${keys.joinToString("\n") {"val $it = Locale.of(\"$it\")"}}
            val all by lazy { listOf(${keys.joinToString(", ")})}
        }
    """.trimIndent()
}