import codegen.i18n.I18NCodeGenPlugin

apply<I18NCodeGenPlugin>()

dependencies {
    //never use project modules in this module
    api(libs.kotlin.coroutines)
    implementation(libs.classgraph)
    implementation(libs.kotlinx.serialization.yaml)
    implementation(libs.bundles.gdx.vfx)
    implementation(libs.gdx.freetype)
}