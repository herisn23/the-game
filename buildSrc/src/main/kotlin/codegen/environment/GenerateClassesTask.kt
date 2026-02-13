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
import kotlin.io.path.name
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
        val models = contextDir.resolve("models")
        val textures = contextDir.resolve("textures")
        return models.toFile().listFiles().filter { it.isDirectory && it.listFiles().isNotEmpty() }.map {
            it.generateBiomeAssets(base, Biome.valueOf(it.name.capitalize()))
        } + textures.generateTextures(base)
    }

    private fun Path.generateTextures(base: Path): ClassInfo {
        val foliagePath = resolve("foliage")
        val foliageFiles = foliagePath.toFile().listFiles().toList()


        fun List<File>.createFoliageTextures() =
            flatMap { directory ->
                val dirName = directory.name
                directory.listFiles().map { tex ->
                    val propName = tex.nameWithoutExtension.normalize().decapitalize()
                    AssetData(
                        propName,
                        base.resolve(name).resolve(foliagePath.name).resolve(directory.name)
                            .resolve(tex.name).pathString,
                        "Texture",
                        key = tex.nameWithoutExtension
                    )
                }
            }

        val foliageTextures = foliageFiles.createFoliageTextures()
        val biomeTextures = toFile().listFiles().filter { !it.isDirectory }.map {
            val propName = it.nameWithoutExtension.normalize().replace("PolygonNatureBiomes", "").decapitalize()
            AssetData(
                propName,
                base.resolve(name).resolve(it.name).pathString,
                "Texture",
                key = it.nameWithoutExtension
            )
        }

        val allTextures = foliageTextures + biomeTextures
        return ClassInfo(
            "EnvTexturesAssetManager",
            pack,
            assetTemplate(
                pack,
                "EnvTexturesAsset",
                allTextures,
                true,
                "TextureAssetManagerLoader",
                assetLoaderParameters = ", params as AssetLoaderParameters<T>",
                imports = listOf(
                    "import org.roldy.core.asset.TextureAssetManagerLoader",
                    "import com.badlogic.gdx.assets.AssetLoaderParameters"
                )
            ) {
                """
                    override val textureMap by lazy {
                        mapOf(
                        ${
                    allTextures.joinToString(",\n") {
                        """
                                "${it.key}" to ${it.property}
                            """.trimIndent()
                    }
                }
                        )
                    }
                """.trimIndent()
            }

        )
    }

    private fun File.generateBiomeAssets(base: Path, biome: Biome): ClassInfo {
        val files = listFiles()

        val g3db = files.filter { it.name.endsWith("g3db") }.map {
            val name = it.nameWithoutExtension.normalize().replace("SM", "").decapitalize()
            AssetData(
                name,
                base.resolve("models").resolve(biome.name.lowercase()).resolve(it.name).pathString,
                "Model",
                it.nameWithoutExtension
            )
        }
        return ClassInfo(
            "${biome.name}AssetManager",
            pack,
            assetTemplate(
                pack,
                biome.name,
                g3db,
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
                                "${it.key}" to ${it.property}
                            """.trimIndent()
                    }
                }
                        )
                    }
                """.trimIndent()
            }

        )
    }

    fun String.normalize() =
        replace("_", "")
            .replace(" ", "")

}