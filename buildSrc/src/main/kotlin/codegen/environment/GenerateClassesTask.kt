package codegen.environment

import codegen.AssetData
import codegen.ClassInfo
import codegen.assetTemplate
import codegen.generateKotlinClasses
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

private const val pack = "org.roldy.g3d.environment"

enum class Biome {
    Alpine,
    Tropical,
    EnchantedForest,
    Meadow,
    Swamp,
    Arid
}

abstract class GenerateClassesTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputDirectory = outputDir.get().asFile
        val classes = generateAssets()
        generateKotlinClasses(outputDirectory, classes)
        logger.lifecycle("Generated classes in: ${outputDirectory.absolutePath}")
    }

    private fun generateAssets(): List<ClassInfo> {
        val base = Path.of("3d/environments")
        val contextDir = Path.of("assets").resolve(base)
        return contextDir.toFile().listFiles().filter { it.isDirectory && it.listFiles().isNotEmpty() }.map {
            it.generateBiomeAssets(base, Biome.valueOf(it.name.capitalize()))
        }
    }

    private fun File.generateBiomeAssets(base: Path, biome: Biome): ClassInfo {
        val files = listFiles()
        val g3db = files.filter { it.name.endsWith("g3db") }.map {
            val name = it.nameWithoutExtension.replace("_", "").replace("SM", "").decapitalize()
            AssetData(
                name,
                base.resolve(biome.name.lowercase()).resolve(it.name).pathString,
                "Model"
            )
        }//.first().let(::listOf)

        fun File.createTextureData(prop: String) =
            AssetData(
                prop,
                base.resolve(biome.name.lowercase()).resolve(name).pathString,
                "Texture"
            )

        val textures = files.filter { it.name.endsWith("png") }.mapNotNull {
            when {
                it.name.contains("Diffuse") -> it.createTextureData("diffuseTexture")
                it.name.contains("Emissive") -> it.createTextureData("emissiveTexture")
                else -> null
            }
        }
        return ClassInfo(
            "${biome.name}AssetManager",
            pack,
            assetTemplate(
                pack,
                biome.name,
                g3db + textures,
                "EnvironmentAssetManagerLoader",
                configureAssetLoader = "configure()"
            ) {
                """
                    override val modelMap by lazy {
                        mapOf(
                         ${
                    g3db.joinToString(",\n") {
                        """
                                "${it.property}" to ${it.property}
                            """.trimIndent()
                    }
                }
                        )
                    }
                """.trimIndent()
            }

        )
    }
}