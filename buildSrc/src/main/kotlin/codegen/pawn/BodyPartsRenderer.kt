package codegen.pawn

import codegen.ClassInfo
import java.nio.file.Path
import kotlin.io.path.readLines

const val pckg = "org.roldy.g3d.pawn.part"
const val face = "Head_All_Elements"
const val helmet = "Head_No_Elements"
const val eyebrows = "Eyebrows"
const val facialhair = "FacialHair"
const val torso = "Torso"
const val arm_upper_right = "Arm_Upper_Right"
const val arm_upper_left = "Arm_Upper_Left"
const val arm_lower_right = "Arm_Lower_Right"
const val arm_lower_left = "Arm_Lower_Left"
const val hand_right = "Hand_Right"
const val hand_left = "Hand_Left"
const val hips = "Hips"
const val leg_right = "Leg_Right"
const val leg_left = "Leg_Left"
val groups = mapOf(
    "FacialHair" to listOf(facialhair),
    "Helmet" to listOf(helmet),
    "Shoulder" to listOf(),
    "Body" to listOf(arm_upper_left, arm_upper_right, torso),
    "Gloves" to listOf()
)

fun generateParts(): List<ClassInfo> {
    val base = Path.of("resources/pawnModelParts").readLines().map {
        it.split("|")
    }
    return listOf(
        base.processPartNames(),
        base.filter { it.contains("Male_Parts") }.processMaleParts()

    )
}

private fun List<List<String>>.processPartNames() =
    ClassInfo(
        "PartNames",
        pckg,
        """
            package $pckg
            object PartNames {
              ${
            joinToString("\n") {
                val last = it.last()
                val propName = last.replace("_", "")
                "val $propName = \"$last\""
            }
        }  
            }
        """.trimIndent()

    )

private fun List<List<String>>.processMaleParts(): ClassInfo =
    ClassInfo(
        "Test",
        pckg,
        """
            package $pckg
            object Test {
              ${
            joinToString("\n") {
                val last = it.last()
                val propName = last.replace("_", "")
                "val $propName = \"$last\""
            }
        }  
            }
        """.trimIndent()

    )