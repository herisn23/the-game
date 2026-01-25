package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.Color

class PawnConfiguration {
    val body = BodyType.Male

    private val maleModel = PawnModelInstance.Data(
        PawnAssetManager.modelMale.get(),
        listOf(
            PawnAssetManager.modelMaleExt.get()
        )
    )
    private val femaleModel = PawnModelInstance.Data(
        PawnAssetManager.modelFemale.get(),
        listOf(
            PawnAssetManager.modelFemaleExt.get(),
        )
    )
    private val models = mapOf(
        BodyType.Male to maleModel,
        BodyType.Female to femaleModel,
    )

    private val testParts = listOf(
        "PT_${body}_Armor_head_01",
        "PT_${body}_Armor_01_A_body",
        "PT_${body}_Armor_01_A_boots",
        "PT_${body}_Armor_01_A_cape",
        "PT_${body}_Armor_01_A_gauntlets",
        "PT_${body}_Armor_01_A_helmet",
        "PT_${body}_Armor_01_A_legs",
//        "PT_${body}_Armor_Ex1_body_21",
//        "PT_${body}_Armor_Ex1_boots_03",
//        "PT_${body}_Armor_Ex1_cape_01",
//        "PT_${body}_Armor_Ex1_gauntlets_02",
//        "PT_${body}_Armor_Ex1_helmet_05",
//        "PT_${body}_Armor_Ex1_legs_01"
    )

    val instances = models.map { (type, data) ->
        type to PawnModelInstance(data, type).apply {
            setVisibility(testParts)
        }
    }.toMap()

    val instance get() = instances.getValue(body)


    var skinColor = Color(2.02193f, 1.0081f, 0.6199315f, 1f)
    var eyesColor = Color(0.0734529f, 0.1320755f, 0.05046281f, 1f)
    var hairColor = Color(0.5943396f, 0.3518379f, 0.1093361f, 1f)
    var scleraColor = Color(0.9056604f, 0.8159487f, 0.8159487f, 1f)
    var lipsColor = Color(0.8301887f, 0.3185886f, 0.2780349f, 1f)
    var scarsColor = Color(0.8490566f, 0.5037117f, 0.3884835f, 1f)

    // Metal colors
    var metal1Color = Color(2f, 0.682353f, 0.1960784f, 1f)
    var metal2Color = Color(0.4674706f, 0.4677705f, 0.5188679f, 1f)
    var metal3Color = Color(0.4383232f, 0.4383232f, 0.4716981f, 1f)

    // Leather colors
    var leather1Color = Color(0.4811321f, 0.2041155f, 0.08851016f, 1f)
    var leather2Color = Color(0.4245283f, 0.190437f, 0.09011215f, 1f)
    var leather3Color = Color(0.1698113f, 0.04637412f, 0.02963688f, 1f)//.mul(2.5f)

    // Cloth colors
    var cloth1Color = Color(0.1465379f, 0.282117f, 0.3490566f, 1f)
    var cloth2Color = Color(1f, 0f, 0f, 1f)
    var cloth3Color = Color(0.8773585f, 0.6337318f, 0.3434941f, 1f)

    // Gems colors
    var gems1Color = Color(0.3773585f, 0f, 0.06650025f, 1f)
    var gems2Color = Color(0.2023368f, 0f, 0.4339623f, 1f)
    var gems3Color = Color(0f, 0.1132075f, 0.01206957f, 1f)

    // Feathers colors
    var feathers1Color = Color(0.7735849f, 0.492613f, 0.492613f, 1f)
    var feathers2Color = Color(0.6792453f, 0f, 0f, 1f)
    var feathers3Color = Color(0f, 0.1793142f, 0.7264151f, 1f)


    // Smoothness values
    var skinSmoothness = 0.3f
    var eyesSmoothness = 0.7f
    var hairSmoothness = 0.1f
    var scleraSmoothness = 0.5f
    var lipsSmoothness = 0.4f
    var scarsSmoothness = 0.3f
    var metal1Smoothness = 0.7f
    var metal2Smoothness = 0.7f
    var metal3Smoothness = 0.7f
    var leather1Smoothness = 0.3f
    var leather2Smoothness = 0.3f
    var leather3Smoothness = 0.3f
    var gems1Smoothness = 1.0f
    var gems2Smoothness = 0.0f
    var gems3Smoothness = 0.0f

    // Metallic values
    var metal1Metallic = 0.65f
    var metal2Metallic = 0.65f
    var metal3Metallic = 0.65f
}