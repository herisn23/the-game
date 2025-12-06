package org.roldy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.roldy.core.Renderable
import org.roldy.equipment.atlas.EquipmentAtlas
import org.roldy.equipment.atlas.armor.Armor
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.*
import org.roldy.equipment.atlas.weapon.*
import org.roldy.keybind.KeybindProcessor
import org.roldy.keybind.KeybindSettings
import org.roldy.keybind.keybinds
import org.roldy.pawn.skeleton.PawnSkeletonManager
import org.roldy.pawn.skeleton.attribute.*
import org.roldy.utils.sequencer

class PawnTest(
    val speed: Float,
    val camera: Camera,
    val batch: SpriteBatch
) : Renderable {

    override val layer: Int
        get() = -1

    private val weapons: List<EquipmentAtlas> by lazy {
        Weapons.all
    }
    private val armors: List<ArmorAtlas> by lazy {
        Armor.all
    }
    private val customizations: List<CustomizationAtlas> by lazy {
        CustomizationAtlas.all
    }
    private val underwears: List<UnderWearAtlas> by lazy {
        Underwear.all
    }
    private val shields: List<ShieldAtlas> by lazy {
        Shield.all
    }
    private val pawnManager: PawnSkeletonManager by lazy {
        PawnSkeletonManager(batch).apply {
            addEventListener(Slash1H) { _, _ ->
                println("hit")
            }
        }
    }
    val inputProcessor = MyInputProcessor(this, keybinds)
    override val zIndex: Float
        get() = pawnManager.zIndex

    context(delta: Float)
    override fun render() {
        camera.position.set(pawnManager.x, pawnManager.y, 0f)
        camera.update()
        test()
        context(delta, this) {
            pawnManager.render()
        }
    }

    val armorsSequencer by sequencer { armors }
    val weaponSequencer by sequencer { Sword.all }
    fun test() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            armorsSequencer.next {
                PawnArmorSlot.Piece.entries.forEach { slot ->
                    pawnManager.setArmor(slot, this)
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            armorsSequencer.prev {
                PawnArmorSlot.Piece.entries.forEach { slot ->
                    pawnManager.setArmor(slot, this)
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            weaponSequencer.next {
                pawnManager.setWeapon(PawnWeaponSlot.MainHand, this)
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            weaponSequencer.prev {
                pawnManager.setWeapon(PawnWeaponSlot.MainHand, this)
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            pawnManager.customize(CustomizablePawnSlotBody.Hair, Hair.CasualMessy1)
            pawnManager.customize(CustomizablePawnSlotBody.Beard, Beard.Type13)
            pawnManager.customize(CustomizablePawnSlotBody.Eyes, Eyes.Girl03)
            pawnManager.customize(CustomizablePawnSlotBody.Head, Body.Type7)
            pawnManager.customize(CustomizablePawnSlotBody.EyeBrows, Eyebrows.Eyebrows15)
            pawnManager.customize(CustomizablePawnSlotBody.Mouth, Mouth.Mouth09)
            pawnManager.customize(CustomizablePawnSlotBody.EarLeft, Ears.Type2)
            pawnManager.customize(CustomizablePawnSlotBody.EarRight, Ears.Type5)
            pawnManager.setWeapon(PawnWeaponSlot.MainHand, Dagger.BronzeDagger)
            pawnManager.setShield(Shield.Bloodmoon)
            pawnManager.setUnderwear(Underwear.FemaleUnderwearType1)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            pawnManager.slash1H(1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            pawnManager.strip()
        }
    }

    override fun dispose() {
        weapons.forEach(EquipmentAtlas::dispose)
        armors.forEach(EquipmentAtlas::dispose)
        customizations.forEach(CustomizationAtlas::dispose)
        underwears.forEach(UnderWearAtlas::dispose)
        shields.forEach(ShieldAtlas::dispose)
        pawnManager.dispose()
        batch.dispose()
    }

    class MyInputProcessor(val app: PawnTest, override val settings: KeybindSettings) : KeybindProcessor {
        var lastKeycode: Int = 0
        val keyActions = mapOf(
            Input.Keys.W to (
                    {
                        app.pawnManager.currentOrientation = Back
                        app.pawnManager.walk(app.speed)
                    } to {
                        app.pawnManager.stop()
                    }),
            Input.Keys.A to (
                    {
                        app.pawnManager.currentOrientation = Left
                        app.pawnManager.walk(app.speed)
                    } to {
                        app.pawnManager.stop()
                    }),
            Input.Keys.S to (
                    {
                        app.pawnManager.currentOrientation = Front
                        app.pawnManager.walk(app.speed)
                    } to {
                        app.pawnManager.stop()
                    }),
            Input.Keys.D to (
                    {
                        app.pawnManager.currentOrientation = Right
                        app.pawnManager.walk(app.speed)
                    } to {
                        app.pawnManager.stop()
                    }
                    )
        )

        override fun keyDown(keycode: Int): Boolean {
            if (keyActions.keys.contains(keycode)) {
                lastKeycode = keycode
            }
            return keyActions[keycode]?.first()?.let { true } ?: false
        }

        override fun keyUp(keycode: Int): Boolean {
            if (keycode != lastKeycode) return false
            return keyActions[keycode]?.second()?.let { true } ?: false
        }
    }

}