package org.roldy.pawn.skeleton.attribute

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import java.util.Locale.getDefault

abstract class CustomizablePawnSlotBody(override val name: String) : PawnBodySkeletonSlot(name) {
    abstract fun findRegion(orientation: PawnSkeletonOrientation, atlas: TextureAtlas): TextureAtlas.AtlasRegion

    abstract class NonPairSlotBody(override val name: String) : CustomizablePawnSlotBody(name) {
        fun regionName(orientation: PawnSkeletonOrientation) =
            orientation.capitalizedName

        override fun findRegion(orientation: PawnSkeletonOrientation, atlas: TextureAtlas): TextureAtlas.AtlasRegion =
            regionName(orientation).let(atlas::findRegion)
    }


    object Head : NonPairSlotBody("head") {
        override fun findRegion(orientation: PawnSkeletonOrientation, atlas: TextureAtlas): TextureAtlas.AtlasRegion =
            regionName(orientation).let {
                atlas.findRegion("${it}Head")
            }
    }

    object Eyes : NonPairSlotBody("eyes")
    object Hair : NonPairSlotBody("hair")
    object EyeBrows : NonPairSlotBody("eyebrows")
    object Beard : NonPairSlotBody("beard")
    object Mouth : NonPairSlotBody("mouth")

    abstract class Ear(name: String) : CustomizablePawnSlotBody(name) {
        fun regionName(orientation: PawnSkeletonOrientation, direction: String): String =
            when (orientation) {
                Left, Right -> direction
                else -> "${orientation.capitalizedName}${
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
        val allParts: List<CustomizablePawnSlotBody> by lazy {
            listOf(
                Head, Hair, Eyes, EyeBrows, Beard, Mouth, EarLeft, EarRight
            )
        }
        val hairSlots by lazy {
            listOf(Hair, Beard)
        }
        val hideableSlotsByArmor: Map<PawnArmorSlot.Piece, List<CustomizablePawnSlotBody>> by lazy {
            mapOf(
                PawnArmorSlot.Piece.Helmet to listOf(
                    Hair, EarLeft, EarRight
                )
            )
        }
    }
}