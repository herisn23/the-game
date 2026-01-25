package codegen.pawn

import codegen.ClassInfo
import codegen.generateKotlinClasses
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

private const val pack = "org.roldy.g3d.pawn"

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
        ) + generateParts()
        generateKotlinClasses(outputDirectory, classes)
        logger.lifecycle("Generated classes in: ${outputDirectory.absolutePath}")
    }


    private fun generateAnimations(): ClassInfo {
        val rootDir = project.rootProject.rootDir
        val base = Path.of("3d/pawn/animations")
        val contextDir = Path.of("assets").resolve(base)
        val absolutePath = rootDir.toPath().resolve(contextDir)
        val p = absolutePath.toFile().listFiles().map { file ->
            val body = file.bodyType()
            body to file.nameWithoutExtension.lowercase()
        }
        return ClassInfo(
            "PawnAnimations",
            pack,
            animTemplate(p.groupBy { it.first }.map {
                it.key to it.value.map { it.second }
            }.toMap())

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
                        "mask${index}",
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
        val models = listOf(
            "modelMale" to "PT_Male_Armors_Modular.g3db",
            "modelMaleExt" to "PT_Male_Armors_Ex1_Modular.g3db",
            "modelMaleExt2" to "PT_Male_Armors_Ex2_Modular.g3db",
            "modelFemale" to "PT_Female_Armors_Modular.g3db",
            "modelFemaleExt" to "PT_Female_Armors_Ex1_Modular.g3db",
            "modelFemaleExt2" to "PT_Female_Armors_Ex2_Modular.g3db"
        ).map { (prop, path) ->
            AssetData(
                prop,
                base.resolve(path).pathString,
                "Model"
            )
        }
        val assetData = masks + anims + models
        return ClassInfo(
            "PawnAssetManager",
            pack,
            assetTemplate(assetData)

        )

    }

    fun animTemplate(anims: Map<String, List<String>>) =
        """
            package $pack
            import com.badlogic.gdx.graphics.g3d.Model
            object PawnAnimations {
                  class Anim(
                    val id: String,
                    val model: PawnAssetManager.Asset<Model>
                  )
                  interface IAnim {
                  ${
            anims.getValue(anims.keys.first()).joinToString("\n") {
                val id = it.replace(anims.keys.first().lowercase(), "")
                "val ${id}: Anim"
            }
        }
                    val all: List<Anim>
                  }
                ${
            anims.keys.joinToString("\n") { body ->
                """
                    object $body: IAnim {
                        ${
                    anims.getValue(body).joinToString("\n") { name ->
                        val id = name.replace(body.lowercase(), "")
                        "override val ${id} = Anim(\"$id\", PawnAssetManager.${name})"
                    }
                }
                        override val all = listOf(${
                    anims.getValue(body).joinToString(", ") {
                        it.replace(body.lowercase(), "")
                    }
                })
                    }
                """.trimIndent()
                
            }
        }
            operator fun get(type: BodyType) = 
        when (type) {
            BodyType.Male -> Male
            BodyType.Female -> Female
        }
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
        val type: String
    )

    private fun File.bodyType() =
        when {
            name.contains("Female") -> "Female"
            name.contains("Male") -> "Male"
            else -> error("Unsupported file type $this")
        }
}