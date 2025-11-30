val assets: SourceSet by sourceSets.creating {
    resources.srcDirs(rootProject.file("assets").path)
}
dependencies {
    implementation("org.yaml:snakeyaml:2.2")
    // https://mvnrepository.com/artifact/tools.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation(libs.bundles.gdx)
    implementation(libs.gdx.backend.headless)
    implementation(libs.gdx.tools)
    runtimeOnly(variantOf(libs.gdx.platform) {
        classifier("natives-desktop")
    })
    runtimeOnly(assets.output)
}