package codegen.gen

import codegen.ClassInfo
import codegen.ClassNameNormalizer
import java.io.File
import java.util.Locale.getDefault

object Customization: Generator {
    const val pack = "org.roldy.equipment.atlas.customization"
    const val dir = "pawn/human/customization"
    val names = listOf("beard", "body", "ears", "eyebrows", "eyes", "hair", "mouth")

    override fun generate(root: File): List<ClassInfo> =
        names.map { name ->
            ClassNameNormalizer.clean()
            val subnames = root.assets.resolve("$dir/$name").toFile().listFiles().filter { it.extension == "atlas" }.map {
                it.nameWithoutExtension
            }
            val className = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
            ClassInfo(
                className,
                pack.replace(".", "/"),
                template(
                    className, subnames.map {
                        ClassNameNormalizer.normalizeUnique(it) to "$dir/$name/$it.atlas"
                    }
                )
            )

        }

    fun template(name: String, names: List<Pair<String, String>>) =
        """
            package $pack
            object $name {
                ${
            names.joinToString("\n\t") {
                """object ${it.first}: CustomizationAtlas("${it.second}")"""
            }
        }
                val all by lazy { listOf(${names.joinToString(", "){it.first}}) }
            }
        """.trimIndent()
}