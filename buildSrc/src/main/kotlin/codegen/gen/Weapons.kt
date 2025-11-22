package codegen.gen

import codegen.ClassInfo
import java.io.File
import java.util.Locale.getDefault

object Weapons : Generator {

    override val pack = "org.roldy.equipment.atlas.weapon"
    override val dir = "weapons"
    val weapons = listOf(
        "axe", "dagger", "hammer", "lance", "staff", "sword", "wand"
    )

    override fun generate(root: File): List<ClassInfo> =
        weapons.map { weapon ->
            val weaponNamesPath = root.assets.resolve("$dir/$weapon").resolve("${weapon.capitalize()}.names")
            val names = weaponNamesPath.toFile().readLines()
            val name = weapon.replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
            ClassInfo(
                name,
                pack.replace(".", "/"),
                template(
                    "$dir/$weapon/$name.atlas", name, names
                )
            )
        }.run {
            this + ClassInfo(
                "Weapons",
                pack.replace(".", "/"),
                """
                        package $pack
                        object Weapons {
                            val all by lazy { listOf(${this.joinToString(", ") { it.name }}) }
                        }
                    """.trimIndent()
            )
        }

    fun template(path: String, name: String, weapons: List<String>) =
        """
package $pack    
import org.roldy.equipment.atlas.EquipmentAtlas

object $name : EquipmentAtlas("$path") {
    ${weapons.joinToString("\n") { objectTemplate(it) }}
    val all by lazy { listOf(${weapons.joinToString(", ") { it }}) }
}
""".trimIndent()

    fun objectTemplate(name: String) =
        """object $name : WeaponRegion("$name", this)"""
}