package org.roldy.pawn

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.ObjectRenderer
import org.roldy.pawn.skeleton.PawnSkeleton
import org.roldy.pawn.skeleton.StrippablePawn
import org.roldy.pawn.skeleton.attribute.ArmorPawnSlot
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSkinSlot
import org.roldy.pawn.skeleton.attribute.Front
import org.roldy.pawn.skeleton.attribute.PawnSkeletonOrientation
import kotlin.properties.Delegates

class Pawn : ObjectRenderer, ArmorWearablePawn, CustomizablePawn, StrippablePawn {
    private val defaultSkinColor = Color.valueOf("FFC878")
    private val defaultHairColor = Color.WHITE
    val skeletons: Map<PawnSkeletonOrientation, PawnSkeleton> = listOf(
        PawnSkeleton(Front, defaultSkinColor, defaultHairColor)
    ).associateBy(PawnSkeleton::orientation)
    var currentOrientation: Front = Front

    override fun setArmor(
        slot: ArmorPawnSlot,
        atlas: TextureAtlas
    ) {
        skeletons.forEach { (_, skel) ->
            skel.setArmor(slot, atlas)
        }
    }

    override fun removeArmor(slot: ArmorPawnSlot) {
        skeletons.forEach { (_, skel) ->
            skel.removeArmor(slot)
        }
    }

    override fun customize(
        slot: CustomizablePawnSkinSlot,
        atlas: TextureAtlas
    ) {
        skeletons.forEach { (_, skel) ->
            skel.customize(slot, atlas)
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