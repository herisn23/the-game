import codegen.CodeGeneratorPlugin

apply<CodeGeneratorPlugin>()

dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.gdx.spine)
}