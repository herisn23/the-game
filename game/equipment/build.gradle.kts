import codegen.equipment.EquipmentCodeGeneratorPlugin

apply<EquipmentCodeGeneratorPlugin>()

dependencies {
    implementation(projects.game.core)
}