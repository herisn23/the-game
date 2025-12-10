import codegen.equipment.EquipmentCodeGeneratorPlugin

apply<EquipmentCodeGeneratorPlugin>()

dependencies {
    implementation(projects.game.layer.rendering.g2d)
}