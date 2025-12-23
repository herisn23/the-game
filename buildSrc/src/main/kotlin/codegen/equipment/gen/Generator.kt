package codegen.equipment.gen

import codegen.ClassInfo
import codegen.equipment.ClassNameNormalizer
import java.io.File
import java.nio.file.Path

interface Generator {
    val pack: String
    val dir: String

    fun generate(root: File): List<ClassInfo>
    val packPath get() = pack.replace(".", "/")
    val File.assets
        get() =
            toPath().resolve("assets")

    fun Path.atlases() =
        toFile().listFiles().filter { it.extension == "atlas" }

    fun File.loadObjectsData(
        normalizeClassName: String.() -> String = { this },
        normalizeAtlasName: String.() -> String = { this }
    ): List<Pair<String, String>> =
        run {
            ClassNameNormalizer.clean()
            assets.resolve(dir).atlases().map { atlas ->
                val shieldName = atlas.nameWithoutExtension
                shieldName.normalizeClassName() to "$dir/${atlas.name}".normalizeAtlasName()
            }
        }

    fun template(objectsData: List<Pair<String, String>>, masterClassName: String, atlasClassName: String) = """
        package $pack
        object $masterClassName {
            ${createAtlasObjects(objectsData, atlasClassName)}
            val all by lazy { listOf(${objectsData.joinToString(",") { it.first }}) }
        }
    """.trimIndent()

    fun createAtlasObjects(list: List<Pair<String, String>>, atlasClassName: String) =
        list.joinToString("\n\t") { (name, atlasPath) ->
            """
                object $name: $atlasClassName("$atlasPath")
            """.trimIndent()
        }

    fun List<Pair<String, String>>.createClassInfo(name: String, atlasClassName: String) =
        ClassInfo(
            name,
            packPath,
            template(this, name, atlasClassName)
        )

    fun String.normalize() = ClassNameNormalizer.normalizeUnique(this)
}