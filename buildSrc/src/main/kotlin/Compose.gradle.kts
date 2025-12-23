import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val assetsName = "assets.data"
val i18nName = "i18n"

val roastPath = "construo/win/roast"
val packageWin = "packageWin"

val copyModules by tasks.registering(Copy::class) {
    dependsOn("installDist")
    mustRunAfter(packageWin)
    layout.buildDirectory.run {
        from(dir("install/launcher/modules"))
        into(dir("$roastPath/modules"))
    }
}

val compressAssets by tasks.registering(Zip::class) {
    from(rootProject.file("assets").path)
    archiveFileName.set(assetsName)
}

val copyAssets by tasks.registering(Copy::class) {
    dependsOn(compressAssets)
    mustRunAfter(packageWin)
    layout.buildDirectory.run {
        from(dir("distributions/$assetsName"))
        into(dir(roastPath))
    }
}

val compressI18n by tasks.registering(Zip::class) {
    from(rootProject.file("i18n").path)
    archiveFileName.set(i18nName)
}

val copyI18N by tasks.registering(Copy::class) {
//    dependsOn(compressI18n)
    mustRunAfter(packageWin)
    layout.buildDirectory.run {
        from(rootProject.file("i18n").path)
        into(dir("$roastPath/i18n"))
    }
}

val editRoastAppJson by tasks.registering {
    mustRunAfter(packageWin)
    doLast {
        val appJson = file(layout.buildDirectory.file("$roastPath/app/app.json"))
        val info = Json.decodeFromString<PackConfig>(appJson.readText())
        val moduleLibs = loadAllLibsToBeOnClassPath("$roastPath/modules")

        info
            .copy(classPath = info.classPath + moduleLibs + listOf(assetsName, i18nName))
            .let(Json::encodeToString)
            .let(appJson::writeText)
    }
}




tasks.register("compose") {
    dependsOn(packageWin, copyI18N, copyAssets, copyModules, editRoastAppJson)
}

fun loadAllLibsToBeOnClassPath(path: String): List<String> =
    layout.buildDirectory.dir(path).map { dir ->
        dir.asFile.list().map {
            "${dir.asFile.name}/${it}"
        }
    }.get()

@Serializable
data class PackConfig(
    val classPath: List<String>,
    val mainClass: String,
    val runOnFirstThread: Boolean,
    val useZgcIfSupportedOs: Boolean,
    val useMainAsContextClassLoader: Boolean,
    val vmArgs: List<String>
)