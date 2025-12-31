package codegen.i18n

import codegen.ClassInfo
import codegen.generateKotlinClasses
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.yamlMap
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.readText

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

        val i18n = rootDir.toPath().resolve("assets").resolve("i18n_config.yaml").readText()
        val yaml = Yaml(
            configuration = YamlConfiguration(
                allowAnchorsAndAliases = true
            )
        )
        val nodes = yaml.parseToYamlNode(i18n).yamlMap.let {
            println(it)
            it.get<YamlMap>("strings")!!
        }

        val info = ClassInfo(
            "Strings",
            "org.roldy.core.i18n",
            templateKeys(nodes.yamlMap.entries.map {(entry, _)->
                val key = entry.content
                """val $key = I18N.Key("$key")""".trimIndent()
            })

        )
        generateKotlinClasses(outputDirectory, listOf(info))

        logger.lifecycle("Generated classes in: ${outputDirectory.absolutePath}")
    }




    fun templateKeys(keys: List<String>) =
        """
        $pack
        
        object Strings {
            ${keys.joinToString("\n")}
        }
    """.trimIndent()
}