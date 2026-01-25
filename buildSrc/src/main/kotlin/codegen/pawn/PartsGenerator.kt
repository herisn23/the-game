package codegen.pawn

import codegen.ClassInfo
import java.nio.file.Path
import kotlin.io.path.readLines

const val pckg = "org.roldy.g3d.pawn.part"

val armorParts = listOf(
    "_body" to "Body",
    "_boots" to "Boots",
    "_cape" to "Cape",
    "_gauntlets" to "Gauntlets",
    "_helmet" to "Helmet",
    "_legs" to "Legs"
)
val bodyParts = listOf(
    "_beard",
    "_hair",
    "_head",
    "_naked"
)

val bydPartsEnums = listOf(
    "Head", "Hair", "Beard", "Body", "Boots", "Gauntlets", "Legs"
)

val male = "Male"
val female = "Female"
fun generateParts(): List<ClassInfo> {
    fun String.filterForParts(parts: List<String>) =
        parts.any { p -> lowercase().contains(p) }

    fun String.filterForArmors() =
        filterForParts(armorParts.map { it.first }) && !contains("naked")

    fun String.filterForBody() =
        filterForParts(bodyParts)

    fun Map<String, List<String>>.checkMissing(ref: Map<String, List<String>>, name: String) {
        forEach { (key, values) ->
            if (ref.getValue(key).size != values.size) {
                error("$name armors missing [$key]")
            }
        }
    }

    val base = Path.of("resources/pawnModelParts").readLines()
    val males = base.filter { it.contains(male) }
    val females = base.filter { it.contains(female) }
    val maleArmorsSets = males.filter { it.filterForArmors() }.collectArmors()
    val femaleArmorsSets = females.filter { it.filterForArmors() }.collectArmors()

    val maleBody = males.filter { it.filterForBody() }.collectBody()
    val femaleBody = females.filter { it.filterForBody() }.collectBody()

    maleArmorsSets.checkMissing(femaleArmorsSets, female)
    femaleArmorsSets.checkMissing(maleArmorsSets, male)

    return listOf(
        base.processPartNames(),
        processArmors(male, maleArmorsSets.restructure(), maleArmorsSets),
        processArmors(female, femaleArmorsSets.restructure(), femaleArmorsSets),
        processBody(male, maleBody),
        processBody(female, femaleBody)
    )
}


private fun List<String>.collectBody() =
    groupBy { name ->
        val newString = name.lowercase()
        bodyParts.first { newString.contains(it) }
    }

private fun List<String>.collectArmors() =
    groupBy { name ->
        var newString = name.lowercase()
        armorParts.forEach {
            newString = newString.replace(it.first, "")
        }
        newString.clean()
    }

private fun String.clean() =
    replace("pt_male_", "")
        .replace("pt_female_", "")
        .replace("_nv", "")

private fun processBody(type: String, part: Map<String, List<String>>) =
    run {
        val naked = part.getValue("_naked")
        val nakedParts = naked.groupBy { name ->
            bydPartsEnums.first { name.contains(it.lowercase()) }
        }
        val allParts = part
            .filter { !it.key.contains("_naked") }
            .map { bydPartsEnums.first { p -> it.key.contains(p.lowercase()) } to it.value }
            .toMap() + nakedParts

        ClassInfo(
            "${type}Body",
            pckg,
            """
            package $pckg
            object ${type}Body: Body {
                override val singleParts = listOf(
                    BodyPart.Boots, BodyPart.Body, BodyPart.Legs, BodyPart.Gauntlets
                )
                override val modularParts = listOf(
                    BodyPart.Hair, BodyPart.Beard, BodyPart.Head
                )
                override val parts:Map<BodyPart, List<String>> = 
                mapOf(
                    ${
                allParts.keys.joinToString(",\n") {
                    """BodyPart.${it} to listOf(${
                        allParts.getValue(it).joinToString(", ") { "\"$it\"" }
                    })""".trimIndent()
                }
            }
                )
            }
        """.trimIndent()
        )
    }


private fun processArmors(
    type: String,
    flatArmors: Map<String, List<String>>,
    sets: Map<String, List<String>>,
) =
    ClassInfo(
        "${type}Armor",
        pckg,
        """
            package $pckg
            object ${type}Armor: Armor {
                override val armors:Map<ArmorPart, List<String>> = 
                mapOf(
                    ${
            flatArmors.keys.joinToString(",\n") {
                "ArmorPart.$it to listOf(${
                    flatArmors.getValue(it).joinToString(separator = ", ") { "\"$it\"" }
                })"
            }
        }
                )
                override val sets: Map<String, List<Pair<ArmorPart, String>>> = 
                mapOf(
                    ${
            sets.keys.joinToString(",\n") { key ->
                """
                           "$key" to listOf(
                                ${
                    sets.getValue(key).joinToString(",") { name ->
                        val armorType = armorParts.first { name.lowercase().contains(it.first) }.second
                        "ArmorPart.$armorType to \"$name\""
                    }
                }
                           )
                       """.trimIndent()
            }
        }
                )
            }
        """.trimIndent()

    )

private fun Map<String, List<String>>.restructure() =
    flatMap { (key, values) ->
        values
    }.groupBy { name ->
        armorParts.first { name.lowercase().contains(it.first) }.second
    }

private fun List<String>.processPartNames() =
    ClassInfo(
        "PartNames",
        pckg,
        """
            package $pckg
            object PartNames {
              ${
            joinToString("\n") { last ->
                val propName = last.replace("_", "").replace("PT", "").decapitalize()
                "val $propName = \"$last\""
            }
        }  
            }
        """.trimIndent()

    )
