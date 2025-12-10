package codegen.equipment.gen

import codegen.equipment.ClassInfo
import codegen.equipment.ClassNameNormalizer
import java.io.File
import java.util.Locale.getDefault

object Customization : Generator {
    override val pack = "org.roldy.rendering.equipment.atlas.customization"
    override val dir = "pawn/human/customization"
    val names = listOf("beard", "body", "ears", "eyebrows", "eyes", "hair", "mouth", "underwear")

    override fun generate(root: File): List<ClassInfo> =
        names.map { name ->
            ClassNameNormalizer.clean()
            val subnames =
                root.assets.resolve("$dir/$name").atlases().map {
                    it.nameWithoutExtension
                }
            val className =
                name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
            val objects = subnames.map {
                ClassNameNormalizer.normalizeUnique(it) to "$dir/$name/$it.atlas"
            }
            objects.createClassInfo(className, "CustomizationAtlas".takeIf { name != "underwear" } ?: "UnderWearAtlas")
        }
}