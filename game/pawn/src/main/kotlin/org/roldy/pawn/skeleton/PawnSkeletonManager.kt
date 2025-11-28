package org.roldy.pawn.skeleton

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.ObjectRenderer
import org.roldy.core.animation.AnimationTypeEventListenerHandler
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.CustomizationAtlas
import org.roldy.equipment.atlas.customization.UnderWearAtlas
import org.roldy.equipment.atlas.weapon.ShieldAtlas
import org.roldy.equipment.atlas.weapon.WeaponRegion
import org.roldy.pawn.customization.*
import org.roldy.pawn.skeleton.attribute.*
import kotlin.properties.Delegates

/**
 * Utility object for pawn movement calculations.
 */
object PawnManagerMath {
    /** Base movement speed in pixels per second when animation speed is 1.0 */
    val baseSpeed = 100f

    /**
     * Calculates the actual movement speed based on animation speed multiplier.
     *
     * @param animationSpeed The animation speed multiplier (e.g., 2.0 for double speed)
     * @return The calculated movement speed in pixels per second
     */
    fun calcMovementSpeed(animationSpeed: Float) = animationSpeed * baseSpeed
}

/**
 * Manager class that coordinates multiple [PawnSkeleton] instances across different orientations.
 *
 * This class maintains one skeleton for each possible orientation (Front, Back, Left, Right) and
 * ensures they stay synchronized in terms of position, equipment, customization, and animations.
 * Only the skeleton matching the current orientation is rendered, but all skeletons are updated
 * to maintain consistent state when orientation changes.
 *
 * The manager implements various interfaces to support equipment, customization, and animations,
 * delegating all operations to the underlying skeleton instances.
 */
class PawnSkeletonManager : AnimationTypeEventListenerHandler<PawnAnimator>(),
    ObjectRenderer,
    ArmorWearer,
    Customizable,
    Strippable,
    WeaponWearer,
    UnderwearWearer,
    ShieldWearer,
    PawnAnimation {

    /** Indicates whether the pawn is currently moving */
    private var moving = false

    /** Public read-only access to movement state */
    val isMoving get() = moving

    /** Current X position of all skeletons */
    val x: Float
        get() = skeletons[currentOrientation]?.skeleton?.x ?: 0f

    /** Current Y position of all skeletons */
    val y: Float
        get() = skeletons[currentOrientation]?.skeleton?.y ?: 0f

    /** Current movement speed multiplier (synced with animation speed) */
    private var movementSpeed = 1f

    /** Default skin color for all pawns */
    val defaultSkinColor: Color = Color.valueOf("FFC878")

    /** Default hair color for all pawns */
    val defaultHairColor: Color = Color.BROWN

    /** Default underwear color for all pawns */
    val defaultUnderWearColor: Color = Color.valueOf("9DA1FF")

    /**
     * Map of all skeleton instances, one for each orientation.
     * All skeletons are kept in sync but only the current orientation is rendered.
     */
    val skeletons: Map<PawnSkeletonOrientation, PawnSkeleton> =
        PawnSkeletonOrientation.Companion.all.map {
            PawnSkeleton(
                it,
                PawnSkeletonData.Companion.instance.getValue(it),
                defaultSkinColor,
                defaultHairColor,
                defaultUnderWearColor
            )
        }.associateBy(PawnSkeleton::orientation)

    /**
     * The current orientation of the pawn, determines which skeleton is rendered.
     * Can be changed at runtime to switch between different directional views.
     */
    var currentOrientation: PawnSkeletonOrientation = Front

    init {
        // Register event listeners for all skeletons
        // Only propagate events from the currently visible orientation
        skeletons.values.forEach { skel ->
            skel.animator.addEventListener(Slash1H) { origin, ev ->
                if (origin.pawn.orientation == currentOrientation) {
                    this.propagate(origin, ev)
                }
            }
        }
    }

    // ===== Weapon Management =====

    /**
     * Equips a weapon to the specified slot on all skeleton orientations.
     *
     * @param slot The weapon slot (e.g., main hand, off-hand)
     * @param region The weapon texture region to display
     */
    override fun setWeapon(
        slot: PawnWeaponSlot,
        region: WeaponRegion
    ) {
        skeletons.forEach { (_, skel) ->
            skel.setWeapon(slot, region)
        }
    }

    /**
     * Removes the weapon from the specified slot on all skeleton orientations.
     *
     * @param slot The weapon slot to clear
     */
    override fun removeWeapon(slot: PawnWeaponSlot) {
        skeletons.forEach { (_, skel) ->
            skel.removeWeapon(slot)
        }
    }

    // ===== Armor Management =====

    /**
     * Equips an armor piece to all skeleton orientations.
     *
     * @param piece The armor piece slot and type
     * @param atlasData The armor atlas containing textures for all orientations
     */
    override fun setArmor(piece: PawnArmorSlot.Piece, atlasData: ArmorAtlas) {
        skeletons.forEach { (_, skel) ->
            skel.setArmor(piece, atlasData)
        }
    }

    /**
     * Removes armor from the specified slot on all skeleton orientations.
     *
     * @param slot The armor slot to clear
     */
    override fun removeArmor(slot: PawnArmorSlot) {
        skeletons.forEach { (_, skel) ->
            skel.removeArmor(slot)
        }
    }

    // ===== Customization Management =====

    /**
     * Applies a customization (e.g., hairstyle, facial features) to all skeleton orientations.
     *
     * @param slot The body slot to customize
     * @param atlasData The customization atlas containing textures
     */
    override fun customize(
        slot: CustomizablePawnSlotBody,
        atlasData: CustomizationAtlas
    ) {
        skeletons.forEach { (_, skel) ->
            skel.customize(slot, atlasData)
        }
    }

    /**
     * Removes customization from the specified slot on all skeleton orientations.
     *
     * @param slot The body slot to clear
     */
    override fun removeCustomization(slot: CustomizablePawnSlotBody) {
        skeletons.forEach { (_, skel) ->
            skel.removeCustomization(slot)
        }
    }

    // ===== Underwear Management =====

    /**
     * Equips underwear to all skeleton orientations.
     *
     * @param atlas The underwear atlas containing textures
     */
    override fun setUnderwear(atlas: UnderWearAtlas) {
        skeletons.forEach { (_, skel) ->
            skel.setUnderwear(atlas)
        }
    }

    /**
     * Removes underwear from all skeleton orientations.
     */
    override fun removeUnderwear() {
        skeletons.forEach { (_, skel) ->
            skel.removeUnderwear()
        }
    }

    // ===== Shield Management =====

    /**
     * Equips a shield to all skeleton orientations.
     *
     * @param atlas The shield atlas containing textures
     */
    override fun setShield(atlas: ShieldAtlas) {
        skeletons.forEach { (_, skel) ->
            skel.setShield(atlas)
        }
    }

    /**
     * Removes the shield from all skeleton orientations.
     */
    override fun removeShield() {
        skeletons.forEach { (_, skel) ->
            skel.removeShield()
        }
    }

    // ===== Color Properties =====

    /**
     * Hair color applied to all skeleton orientations.
     * Changes are automatically propagated when set.
     */
    override var hairColor: Color by Delegates.observable(defaultHairColor) { property, oldValue, newValue ->
        skeletons.forEach { (_, skel) ->
            skel.hairColor = newValue
        }
    }

    /**
     * Skin color applied to all skeleton orientations.
     * Changes are automatically propagated when set.
     */
    override var skinColor: Color by Delegates.observable(defaultSkinColor) { property, oldValue, newValue ->
        skeletons.forEach { (_, skel) ->
            skel.skinColor = newValue
        }
    }

    /**
     * Underwear color applied to all skeleton orientations.
     * Changes are automatically propagated when set.
     */
    override var underwearColor: Color by Delegates.observable(defaultSkinColor) { property, oldValue, newValue ->
        skeletons.forEach { (_, skel) ->
            skel.underwearColor = newValue
        }
    }

    /**
     * Removes all equipment and customization from all skeleton orientations,
     * returning them to their base appearance.
     */
    override fun strip() {
        skeletons.forEach { (_, skel) ->
            skel.strip()
        }
    }

    // ===== Rendering =====

    /**
     * Updates animations for all skeletons and renders the current orientation.
     *
     * All skeletons are animated (even non-visible ones) to maintain consistent state
     * when orientation changes. Movement is processed if the pawn is currently moving.
     *
     * @receiver deltaTime The time elapsed since last frame in seconds
     * @receiver batch The sprite batch used for rendering
     */
    context(deltaTime: Float, batch: SpriteBatch)
    override fun render() {
        skeletons.values.forEach { skel ->
            skel.animate() // Always process animations to preserve states between orientations
        }
        skeletons[currentOrientation]?.run {
            render()
        }
        if (moving) {
            move()
        }
    }

    // ===== Animation Control =====

    /**
     * Transitions all skeletons to idle animation.
     * Stops any movement and plays the idle loop.
     */
    override fun idle() {
        skeletons.forEach { (_, skel) ->
            skel.animation.idle()
        }
    }

    /**
     * Plays the slash attack animation on all skeletons.
     * This animation plays on a separate track and doesn't interrupt movement.
     *
     * @param speed Animation speed multiplier (1.0 = normal speed, 2.0 = double speed)
     */
    override fun slash1H(speed: Float) {
        skeletons.forEach { (_, skel) ->
            skel.animation.slash1H(speed)
        }
    }

    /**
     * Starts walking animation and enables movement for all skeletons.
     * Movement direction is determined by [currentOrientation].
     *
     * @param speed Animation and movement speed multiplier
     */
    override fun walk(speed: Float) {
        moving = true
        movementSpeed = speed
        skeletons.forEach { (_, skel) ->
            skel.animation.walk(speed)
        }
    }

    /**
     * Stops movement and transitions to idle pose.
     * Smoothly blends animations back to their rest positions.
     */
    override fun stop() {
        moving = false
        skeletons.forEach { (_, skel) ->
            skel.animation.stop()
        }
    }

    // ===== Movement Logic =====

    /**
     * Updates skeleton positions based on current orientation and movement speed.
     *
     * Movement directions:
     * - Back orientation: moves up (positive Y)
     * - Front orientation: moves down (negative Y)
     * - Left orientation: moves left (negative X)
     * - Right orientation: moves right (positive X)
     *
     * All skeletons move together to maintain position synchronization.
     *
     * @receiver deltaTime The time elapsed since last frame in seconds
     */
    context(deltaTime: Float)
    private fun move() {
        val speed = PawnManagerMath.calcMovementSpeed(movementSpeed) * deltaTime

        when (currentOrientation) {
            Back -> {
                // Move up - update all skeletons together
                skeletons.values.forEach { it.skeleton.y += speed }
            }

            Front -> {
                // Move down
                skeletons.values.forEach { it.skeleton.y -= speed }
            }

            Left -> {
                // Move left
                skeletons.values.forEach { it.skeleton.x -= speed }
            }

            Right -> {
                // Move right
                skeletons.values.forEach { it.skeleton.x += speed }
            }
        }
    }
}