import codegen.gui.GUICodeGenPlugin

apply<GUICodeGenPlugin>()
dependencies {
    implementation(projects.game.layer.data)
    implementation(projects.game.layer.rendering.g2d)
    implementation(libs.gdx.freetype)
}