import codegen.i18n.I18NCodeGenPlugin

apply<I18NCodeGenPlugin>()

dependencies {
    //never use project modules in this module
    api(libs.kotlin.coroutines)
    api(libs.gdx.spine)
}