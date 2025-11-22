package org.roldy.pawn.skeleton.attribute

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import java.util.Locale.getDefault

abstract class CustomizablePawnSkinSlot(override val name: String) : SkinPawnSkeletonSlot(name) {
    abstract fun findRegion(orientation: PawnSkeletonOrientation, atlas: TextureAtlas): TextureAtlas.AtlasRegion

    abstract class NonPairSlot(override val name: String) : CustomizablePawnSkinSlot(name) {
        internal open fun regionName(orientation: PawnSkeletonOrientation) =
            when (orientation) {
                Left, Right -> "left"
                else -> orientation.value
            }

        override fun findRegion(orientation: PawnSkeletonOrientation, atlas: TextureAtlas): TextureAtlas.AtlasRegion =
            regionName(orientation).let(atlas::findRegion)
    }


    object Head : NonPairSlot("head") {
        override fun findRegion(orientation: PawnSkeletonOrientation, atlas: TextureAtlas): TextureAtlas.AtlasRegion =
            regionName(orientation).let {
                atlas.findRegion("${it}Head")
            }
    }

    object Eyes : NonPairSlot("eyes")
    object Hair : NonPairSlot("hair")
    object EyeBrows : NonPairSlot("eyebrows")
    object Beard : NonPairSlot("beard")
    object Mouth : NonPairSlot("mouth")

    abstract class Ear(name: String) : CustomizablePawnSkinSlot(name) {
        fun regionName(orientation: PawnSkeletonOrientation, direction: String): String =
            when (orientation) {
                Left, Right -> direction
                else -> "${orientation.value}${
                    direction.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            getDefault()
                        ) else it.toString()
                    }
                }"
            }
    }

    object EarLeft : Ear("earLeft") {
        override fun findRegion(
            orientation: PawnSkeletonOrientation,
            atlas: TextureAtlas
        ): TextureAtlas.AtlasRegion =
            regionName(orientation, "left").let(atlas::findRegion)

    }

    //some ears have different right ear so we must do some logic
    object EarRight : Ear("earRight") {
        override fun findRegion(orientation: PawnSkeletonOrientation, atlas: TextureAtlas): TextureAtlas.AtlasRegion =
            regionName(orientation, "right").let {
                atlas.findRegion(it) ?: regionName(orientation, "left").let(atlas::findRegion)
            }
    }

    companion object {
        val allParts: List<CustomizablePawnSkinSlot> by lazy {
            listOf(
                Head, Hair, Eyes, EyeBrows, Beard, Mouth, EarLeft, EarRight
            )
        }
        val hairSlots by lazy {
            listOf(Hair, Beard)
        }
        val hideableSlotsByArmor: Map<ArmorPawnSlot.Piece, List<CustomizablePawnSkinSlot>> by lazy {
            mapOf(
                ArmorPawnSlot.Piece.Helmet to listOf(
                    Hair, EarLeft, EarRight
                )
            )
        }
    }
}