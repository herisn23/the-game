import codegen.tiles.DecorTexturesCodeGenPlugin

apply<DecorTexturesCodeGenPlugin>()
dependencies {
    implementation(libs.gdx.freetype)
}