import codegen.decor.DecorTexturesCodeGenPlugin

apply<DecorTexturesCodeGenPlugin>()
dependencies {
    implementation(projects.game.layer.data)
    implementation(projects.game.layer.rendering.g2d)
}