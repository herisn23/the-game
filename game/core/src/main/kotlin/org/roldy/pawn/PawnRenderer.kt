package org.roldy.pawn

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.ObjectRenderer
import org.roldy.animation.AnimationTypeEventListenerHandler
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.CustomizationAtlas
import org.roldy.equipment.atlas.customization.UnderWearAtlas
import org.roldy.equipment.atlas.weapon.ShieldAtlas
import org.roldy.equipment.atlas.weapon.WeaponRegion
import org.roldy.pawn.skeleton.PawnAnimation
import org.roldy.pawn.skeleton.PawnAnimator
import org.roldy.pawn.skeleton.PawnSkeleton
import org.roldy.pawn.skeleton.PawnSkeletonData
import org.roldy.pawn.skeleton.attribute.*
import kotlin.properties.Delegates

class PawnRenderer : AnimationTypeEventListenerHandler<PawnAnimator>(), ObjectRenderer,
    ArmorWearer,
    Customizable,
    Strippable,
    WeaponWearer,
    UnderwearWearer,
    ShieldWearer, PawnAnimation {
    val defaultSkinColor: Color = Color.valueOf("FFC878")
    val defaultHairColor: Color = Color.BROWN
    val defaultUnderWearColor: Color = Color.valueOf("9DA1FF")
    val skeletons: Map<PawnSkeletonOrientation, PawnSkeleton> =
        PawnSkeletonOrientation.all.map {
            PawnSkeleton(
                it,
                PawnSkeletonData.instance.getValue(it),
                defaultSkinColor,
                defaultHairColor,
                defaultUnderWearColor
            )
        }.associateBy(PawnSkeleton::orientation)
    var currentOrientation: PawnSkeletonOrientation = Front


    init {
        skeletons.values.forEach { skel ->
            skel.animator.addEventListener(Slash1H) { origin, ev ->
                if (origin.pawn.orientation == currentOrientation) {
                    this.propagate(origin, ev)
                }
            }
        }
    }

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

    override fun setArmor(piece: ArmorPawnSlot.Piece, atlasData: ArmorAtlas) {
        skeletons.forEach { (_, skel) ->
            skel.setArmor(piece, atlasData)
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

    override fun setUnderwear(atlas: UnderWearAtlas) {
        skeletons.forEach { (_, skel) ->
            skel.setUnderwear(atlas)
        }
    }

    override fun removeUnderwear() {
        skeletons.forEach { (_, skel) ->
            skel.removeUnderwear()
        }
    }

    override fun setShield(atlas: ShieldAtlas) {
        skeletons.forEach { (_, skel) ->
            skel.setShield(atlas)
        }
    }

    override fun removeShield() {
        skeletons.forEach { (_, skel) ->
            skel.removeShield()
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
        skeletons.values.forEach { skel ->
            skel.animate() //always process animations to simply preserve states
        }
        skeletons[currentOrientation]?.run {
            render()
        }
    }

    //animations
    override fun idle() {
        skeletons.forEach { (_, skel) ->
            skel.animation.idle()
        }
    }

    override fun slash1H() {
        skeletons.forEach { (_, skel) ->
            skel.animation.slash1H()
        }
    }

    override fun walk() {
        skeletons.forEach { (_, skel) ->
            skel.animation.walk()
        }
    }

    override fun stop() {
        skeletons.forEach { (_, skel) ->
            skel.animation.stop()
        }
    }
}