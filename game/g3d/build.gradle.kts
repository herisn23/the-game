import codegen.pawn.PawnCodeGenPlugin

apply<PawnCodeGenPlugin>()
dependencies {
    implementation(projects.game.core)
}