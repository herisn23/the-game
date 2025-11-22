package codegen.gen

import codegen.ClassInfo
import codegen.ClassNameNormalizer
import java.io.File


object Armors: Generator {
    const val pack = "org.roldy.equipment.atlas.armor"
    const val dir = "pawn/human/armor"

    override fun generate(root: File): List<ClassInfo> =
        root.assets.resolve(dir).toFile().listFiles().filter { it.extension == "atlas" }.run {
            ClassNameNormalizer.clean()
            map { atlas ->
                val name = atlas.nameWithoutExtension.normalize()
                ClassInfo(
                    name,
                    pack.replace(".", "/"),
                    template(
                        name,"$dir/${atlas.nameWithoutExtension}", pack
                    )
                )
            }
        }.run {
            this + listOf(
                ClassInfo(
                    "Armors",
                    pack.replace(".", "/"),
                    """
                        package $pack
                        object Armors {
                            val all by lazy { listOf(${this.joinToString(", ") {it.name}}) }
                        }
                    """.trimIndent()

                )
            )
        }


    fun String.normalize() = ClassNameNormalizer.normalizeUnique(this)

    fun template(name: String, path: String, pckg: String) =
        """
            package $pckg
            object $name: ArmorAtlas("$path")
        """.trimIndent()
}