import io.github.fourlastor.construo.Target
import java.util.*

val javaVersion: String by project
val os: String = System.getProperty("os.name").lowercase(Locale.ROOT)

plugins {
    id("Compose")
    alias(libs.plugins.construo) apply true
    application
}

application {
    mainClass.set("org.roldy.launcher.LauncherKt")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

val assets: SourceSet by sourceSets.creating {
    resources.srcDirs(rootProject.file("assets").path)
}

val i18n: SourceSet by sourceSets.creating {
    resources.srcDirs(rootProject.file("i18n").path)
}

dependencies {
    implementation(libs.bundles.gdx)
    runtimeOnly(variantOf(libs.gdx.platform) {
        classifier("natives-desktop")
    })
    runtimeOnly(variantOf(libs.gdx.freetype.platform) {
        classifier("natives-desktop")
    })
    implementation(projects.game.application)

    //exclude assets from compileTime we only need it at runtime
    runtimeOnly(assets.output)
    runtimeOnly(i18n.output)
}


tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    val moduleJars = rootProject.allprojects.map {
        it.tasks.jar.get().archiveFileName.get()
    }
    // Include all dependencies inside the JAR (fat JAR)
    from({
        configurations.runtimeClasspath.get().filter {
            it.name.endsWith("jar")
                    //exclude module jars to be added to jar, they are loaded at runtime
                    && !moduleJars.contains(it.name)
        }.map { zipTree(it) }
    })

    // Avoid duplicate files from dependencies
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Add assets to the distribution
distributions {
    main {
        contents {
            from(rootProject.file("assets")) {
                into("assets")   // will appear as build/distributions/my-app/assets
            }

            from(rootProject.file("i18n")) {
                into("i18n")   // will appear as build/distributions/my-app/assets
            }

            fun Project.inGameFolder(): Boolean =
                if (parent?.name == "game") {
                    true
                } else if (parent != null) {
                    parent!!.inGameFolder()
                } else {
                    false
                }

            rootProject.allprojects.forEach {
                if (it.inGameFolder())
                    from(it.tasks.jar) {
                        into("modules")
                    }
            }
        }
    }
}

construo {
    // name of the executable
    name.set("app")
    // human-readable name, used for example in the `.app` name for macOS
    humanName.set("appName")
    roast {
//        vmArgs = listOf("hovno")
    }
    // targets
    targets {
        create<Target.Windows>("win") {
            architecture.set(Target.Architecture.X86_64)
            icon.set(project.file("icons/logo.png"))
            jdkUrl.set("https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.9%2B10/OpenJDK21U-jdk_x64_windows_hotspot_21.0.9_10.zip")
            // run app with console to see logs
            useConsole.set(true)
            useGpuHint.set(false)
        }
    }
}