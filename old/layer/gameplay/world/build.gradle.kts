dependencies {
    implementation(libs.kotlinx.serialization.yaml)
    implementation(projects.old.layer.data)
    implementation(projects.old.layer.rendering.map)
    implementation(projects.old.layer.rendering.environment)
    implementation(projects.old.layer.rendering.screen)
    implementation(projects.old.layer.rendering.g2d)
    implementation(projects.old.layer.rendering.pawn)
    implementation(projects.old.layer.rendering.gui)

    implementation(projects.old.layer.gameplay.state)
}