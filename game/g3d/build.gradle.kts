import codegen.environment.EnvCodeGenPlugin
import codegen.pawn.PawnCodeGenPlugin

apply<PawnCodeGenPlugin>()
apply<EnvCodeGenPlugin>()
dependencies {
    implementation(projects.game.core)
}