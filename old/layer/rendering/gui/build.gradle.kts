import codegen.gui.GUICodeGenPlugin

apply<GUICodeGenPlugin>()
dependencies {
    implementation(projects.old.layer.data)
    implementation(projects.old.layer.rendering.g2d)
    implementation(libs.gdx.freetype)
}