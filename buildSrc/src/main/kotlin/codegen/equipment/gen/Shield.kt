package codegen.equipment.gen

import codegen.ClassInfo
import java.io.File

object Shield : Generator {
    override val pack = "org.roldy.rendering.equipment.atlas.weapon"
    override val dir = "weapons/shield"
    override fun generate(root: File): List<ClassInfo> =
        listOf(
            root.loadObjectsData().createClassInfo("Shield", "ShieldAtlas")
        )


}