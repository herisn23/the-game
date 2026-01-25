package codegen.pawn

import codegen.ClassInfo
import java.nio.file.Path
import kotlin.io.path.readLines

const val pckg = "org.roldy.g3d.pawn.part"

fun GenerateClassesTask.generateParts(): ClassInfo {
    val rootDir = project.rootProject.rootDir
    val base = Path.of("resources/pawnModelParts").readLines().map {
        it.split("|")
    }
    println(base)
    return ClassInfo(
        "PartNames",
        pckg,
        """
            package $pckg
            object PartNames {
              ${
            base.joinToString("\n") {
                val last = it.last()
                val propName = last.replace("_", "")
                "val $propName = \"$last\""
            }
        }  
            }
        """.trimIndent()

    )
}