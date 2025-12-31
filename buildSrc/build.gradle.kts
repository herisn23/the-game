plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.construo)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.yaml)
}

repositories {
    gradlePluginPortal()
}