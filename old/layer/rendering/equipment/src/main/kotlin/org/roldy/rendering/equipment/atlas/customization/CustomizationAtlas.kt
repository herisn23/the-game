package org.roldy.rendering.equipment.atlas.customization

import org.roldy.rendering.equipment.atlas.EquipmentAtlas

abstract class CustomizationAtlas(atlasPath: String) : EquipmentAtlas(atlasPath) {
    companion object {
        val all by lazy {
            Hair.all + Beard.all + Eyes.all + Body.all + Mouth.all + Ears.all + Eyebrows.all
        }
    }
}