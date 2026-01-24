import codegen.equipment.EquipmentCodeGeneratorPlugin

apply<EquipmentCodeGeneratorPlugin>()

dependencies {
    implementation(projects.old.layer.rendering.g2d)
}