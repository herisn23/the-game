import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "org.roldy"
version = "1.0-SNAPSHOT"

val javaVersion: String by project

allprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.serialization.get().pluginId)
    apply(plugin = "kotlin")
    repositories {
        mavenCentral()
    }
    tasks.test {
        useJUnitPlatform()
    }
    kotlin {
        jvmToolchain(javaVersion.toInt())
        compilerOptions {
            languageVersion.set(KotlinVersion.KOTLIN_2_2)
            apiVersion.set(KotlinVersion.KOTLIN_2_2)
            freeCompilerArgs.addAll(
                listOf(
                    "-Xcontext-sensitive-resolution",
                    "-Xcontext-parameters"
                )
            )
        }
    }
}

