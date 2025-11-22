package codegen.gen

import codegen.ClassInfo
import java.io.File

interface Generator {
    fun generate(root: File):List<ClassInfo>

    val File.assets get() =
        toPath().resolve("assets")
}