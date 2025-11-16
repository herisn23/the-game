import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.plugins


plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.construo)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}

repositories {
    gradlePluginPortal()
}