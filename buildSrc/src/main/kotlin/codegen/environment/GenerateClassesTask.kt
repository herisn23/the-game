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
        fun String.onlyOneModel() =
            contains("Bld_Giant_Column_01") || contains("Env_Grass_Med_Clump_01") || contains("Env_Tree_Forest_02")

        fun String.normalize() =
            replace("_", "")
                .replace(" ", "")
                .replace("Occlusion", "")
                .replace("occlusion", "")
                .replace("Normals", "")
                .replace("normals", "")

        val g3db = files.filter { it.name.endsWith("g3db") && it.name.onlyOneModel() }.map {
            val name = it.nameWithoutExtension.normalize().replace("SM", "").decapitalize()
            AssetData(
                name,
                base.resolve(biome.name.lowercase()).resolve(it.name).pathString,
                "Model"
            )
        }

        fun File.createTextureData(prop: String) =
            AssetData(
                prop,
                base.resolve(biome.name.lowercase()).resolve(name).pathString,
                "Texture"
            )
        val foliageDirectories = files.filter { it.isDirectory }

        fun List<File>.createFoliageTextures() =
            flatMap { directory ->
                val dirName = directory.name
                directory.listFiles().map { tex ->
                    val propName = "$dirName${tex.nameWithoutExtension.normalize().capitalize()}"
                    AssetData(
                        propName,
                        base.resolve(biome.name.lowercase()).resolve(directory.name).resolve(tex.name).pathString,
                        "Texture"
                    )
                }
            }

        val foliageTextures = foliageDirectories.createFoliageTextures()

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
                g3db + textures + foliageTextures,
                true,
                "EnvironmentAssetManagerLoader",
                configureAssetLoader = """
                    val loader = SyntyModelLoader(fileHandleResolver)
                    setLoader(Model::class.java, ".g3db", loader)
                """.trimIndent(),
                imports = listOf(
                    "import org.roldy.core.asset.SyntyModelLoader"
                )
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