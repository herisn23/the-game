package org.roldy.pawn

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.ObjectRenderer
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.CustomizationAtlas
import org.roldy.equipment.atlas.underwear.UnderWearAtlas
import org.roldy.equipment.atlas.weapon.WeaponRegion
import org.roldy.pawn.skeleton.PawnSkeleton
import org.roldy.pawn.skeleton.StrippablePawn
import org.roldy.pawn.skeleton.attribute.*
import kotlin.properties.Delegates

class PawnRenderer : ObjectRenderer, ArmorWearablePawn, CustomizablePawn, StrippablePawn, WeaponWearablePawn,
    UnderwearPawn {
    val defaultSkinColor: Color = Color.valueOf("FFC878")
    val defaultHairColor: Color = Color.BROWN
    val defaultUnderWearColor: Color = Color.valueOf("9DA1FF")
    val skeletons: Map<PawnSkeletonOrientation, PawnSkeleton> = listOf(
        PawnSkeleton(Front, defaultSkinColor, defaultHairColor, defaultUnderWearColor)
    ).associateBy(PawnSkeleton::orientation)
    var currentOrientation: Front = Front

    override fun setWeapon(
        slot: WeaponPawnSlot,
        region: WeaponRegion
    ) {
        skeletons.forEach { (_, skel) ->
            skel.setWeapon(slot, region)
        }
    }

    override fun removeWeapon(slot: WeaponPawnSlot) {
        skeletons.forEach { (_, skel) ->
            skel.removeWeapon(slot)
        }
    }

    override fun setArmor(slot: ArmorPawnSlot, atlasData: ArmorAtlas) {
        skeletons.forEach { (_, skel) ->
            skel.setArmor(slot, atlasData)
        }
    }

    override fun removeArmor(slot: ArmorPawnSlot) {
        skeletons.forEach { (_, skel) ->
            skel.removeArmor(slot)
        }
    }

    override fun customize(
        slot: CustomizablePawnSkinSlot,
        atlasData: CustomizationAtlas
    ) {
        skeletons.forEach { (_, skel) ->
            skel.customize(slot, atlasData)
        }
    }

    override fun removeCustomization(slot: CustomizablePawnSkinSlot) {
        skeletons.forEach { (_, skel) ->
            skel.removeCustomization(slot)
        }
    }

    override fun setUnderWear(atlas: UnderWearAtlas) {
        skeletons.forEach { (_, skel) ->
            skel.setUnderWear(atlas)
        }
    }

    override fun removeUnderwear() {
        skeletons.forEach { (_, skel) ->
            skel.removeUnderwear()
        }
    }


    override var hairColor: Color by Delegates.observable(defaultHairColor) { property, oldValue, newValue ->
        skeletons.forEach { (_, skel) ->
            skel.hairColor = newValue
        }
    }
    override var skinColor: Color by Delegates.observable(defaultSkinColor) { property, oldValue, newValue ->
        skeletons.forEach { (_, skel) ->
            skel.skinColor = newValue
        }
    }

    override var underwearColor: Color by Delegates.observable(defaultSkinColor) { property, oldValue, newValue ->
        skeletons.forEach { (_, skel) ->
            skel.underwearColor = newValue
        }
    }

    override fun strip() {
        skeletons.forEach { (_, skel) ->
            skel.strip()
        }
    }

    context(deltaTime: Float, batch: SpriteBatch)
    override fun render() {
        skeletons[currentOrientation]?.run {
            render()
        }
    }
}