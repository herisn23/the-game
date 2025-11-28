import codegen.CodeGeneratorPlugin

apply<CodeGeneratorPlugin>()

dependencies {
    implementation(projects.game.core)
}