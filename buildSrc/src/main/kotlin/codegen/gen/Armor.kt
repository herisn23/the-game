package codegen.gen

import codegen.ClassInfo
import java.io.File


object Armors : Generator {
    override val pack = "org.roldy.equipment.atlas.armor"
    override val dir = "pawn/human/armor"

    override fun generate(root: File): List<ClassInfo> =
        listOf(
            root.loadObjectsData({ normalize() }, { replace(".atlas", "") }).createClassInfo("Armor", "ArmorAtlas")
        )
}