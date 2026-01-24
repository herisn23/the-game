package codegen.pawn

import codegen.ClassInfo
import codegen.generateKotlinClasses
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import kotlin.io.path.pathString

const val pack = "org.roldy.g3d.pawn"

abstract class GenerateClassesTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputDirectory = outputDir.get().asFile

        // Clean and create output directory
        outputDirectory.deleteRecursively()
        outputDirectory.mkdirs()

        val classes = listOf(
            generateAssets(),
            generateAnimations()
        )
        generateKotlinClasses(outputDirectory, classes)
        logger.lifecycle("Generated classes in: ${outputDirectory.absolutePath}")
    }


    private fun generateAnimations(): ClassInfo {
        val rootDir = project.rootProject.rootDir
        val base = Path.of("3d/pawn/animations")
        val contextDir = Path.of("assets").resolve(base)
        val absolutePath = rootDir.toPath().resolve(contextDir)
        val p = absolutePath.toFile().listFiles().map { file ->
            file.nameWithoutExtension.lowercase()
        }
        return ClassInfo(
            "PawnAnimations",
            pack,
            animTemplate(p)

        )
    }

    private fun generateAssets(): ClassInfo {
        val base = Path.of("3d/pawn")
        val rootDir = project.rootProject.rootDir
        val contextDir = Path.of("assets").resolve(base)
        val absolutePath = rootDir.toPath().resolve(contextDir)
        val masks =
            absolutePath.toFile().listFiles().filter { it.name.contains("Mask") }.sortedBy { it.nameWithoutExtension }
                .mapIndexed { index, file ->
                    AssetData(
                        "mask${index + 1}",
                        base.resolve(file.name).pathString,
                        "Texture"
                    )
                }
        val anims = absolutePath.resolve("animations").toFile().listFiles().mapIndexed { index, file ->
            AssetData(
                file.nameWithoutExtension.lowercase(),
                base.resolve("animations").resolve(file.name).pathString,
                "Model"
            )
        }
        val baseModel = listOf(
            AssetData(
                "model",
                base.resolve("Pawn.g3db").pathString,
                "Model"
            )
        )
        val assetData = masks + baseModel + anims
        return ClassInfo(
            "PawnAssetManager",
            pack,
            assetTemplate(assetData)

        )

    }

    fun animTemplate(anims: List<String>) =
        """
            package $pack
            import com.badlogic.gdx.graphics.g3d.Model
            object PawnAnimations {
                  class Anim(
                    val id: String,
                    val model: PawnAssetManager.Asset<Model>
                  ) {
                  
                  }
                ${
            anims.joinToString("\n") {
                "val ${it} = Anim(\"$it\", PawnAssetManager.${it})"
            }
        }
            val all = mapOf(${anims.joinToString(", ") { "\"$it\" to $it" }})
            }
        """.trimIndent()


    fun assetLoadTemplate(path: String, type: String) =
        "load($path, $type::class.java)"

    fun assetTemplate(
        asssets: List<AssetData>
    ) =
        """
package $pack
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
object PawnAssetManager {
        class Asset<T>(
         internal val path: String
        ) {
            fun get(): T = assetManager.get<T>(path)
        }
        ${
            asssets.joinToString("\n") {
                "val ${it.property} = Asset<${it.type}>(\"${it.path}\")"
            }
        }
    val assetManager by lazy {
        AssetManager().apply {
            ${
            asssets.joinToString("\n") {
                assetLoadTemplate("${it.property}.path", it.type)
            }
        }
        }
    }
}
    """.trimIndent()

    fun List<String>.asString() =
        joinToString("\n")

    data class AssetData(
        val property: String,
        val path: String,
        val type: String,
    )
}