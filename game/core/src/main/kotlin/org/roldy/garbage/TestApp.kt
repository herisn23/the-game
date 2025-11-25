package org.roldy.garbage

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import org.roldy.equipment.atlas.EquipmentAtlas
import org.roldy.equipment.atlas.armor.Armor
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.*
import org.roldy.equipment.atlas.weapon.*
import org.roldy.listener.DefaultApplicationListener
import org.roldy.pawn.PawnPresenter
import org.roldy.pawn.skeleton.attribute.*
import org.roldy.utils.invoke
import org.roldy.utils.sequencer
import org.roldy.utils.toggle

class TestApp(
    private val default: DefaultApplicationListener = DefaultApplicationListener()
) : ApplicationListener by default {
    private lateinit var pawnPresenter: PawnPresenter
    private lateinit var font: BitmapFont

    lateinit var weapons: List<EquipmentAtlas>
    lateinit var armors: List<ArmorAtlas>
    lateinit var customizations: List<CustomizationAtlas>
    lateinit var underwears: List<UnderWearAtlas>
    lateinit var shields: List<ShieldAtlas>

    class MyInputProcessor(val app: TestApp) : InputAdapter() {
        var lastKeycode: Int = 0
        val keyActions = mapOf(
            Input.Keys.W to (
                    {
                        app.pawnPresenter.currentOrientation = Back
                        app.pawnPresenter.walk()
                    } to {
                        app.pawnPresenter.stop()
                    }),
            Input.Keys.A to (
                    {
                        app.pawnPresenter.currentOrientation = Left
                        app.pawnPresenter.walk()
                    } to {
                        app.pawnPresenter.stop()
                    }),
            Input.Keys.S to (
                    {
                        app.pawnPresenter.currentOrientation = Front
                        app.pawnPresenter.walk()
                    } to {
                        app.pawnPresenter.stop()
                    }),
            Input.Keys.D to (
                    {
                        app.pawnPresenter.currentOrientation = Right
                        app.pawnPresenter.walk()
                    } to {
                        app.pawnPresenter.stop()
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

    override fun create() {
        default.create()
        Gdx.input.inputProcessor = MyInputProcessor(this)
        armors = Armor.all
        weapons = Weapons.all
        customizations = CustomizationAtlas.all
        underwears = Underwear.all
        shields = Shield.all

        pawnPresenter = PawnPresenter().apply {
            addEventListener(Slash1H) { _, _ ->
                println("hit")
            }
        }
        font = BitmapFont()
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        default.render()
        test()
        default.batch {
            context(delta, this) {
                pawnPresenter.render()
            }

            font.draw(default.batch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, Gdx.graphics.height - 10f)
            font.draw(default.batch, "Delta: ${Gdx.graphics.deltaTime}", 10f, Gdx.graphics.height - 30f)
            font.draw(default.batch, "Memory: ${Gdx.app.javaHeap / 1024 / 1024} MB", 10f, Gdx.graphics.height - 50f)
        }
    }

    var toggleGloves = toggle(
        onTrue = {
            pawnPresenter.setArmor(PawnArmorSlot.Piece.Gloves, Armor.NordicHunterArmorHeavy1)
        },
        onFalse = {
            pawnPresenter.setArmor(PawnArmorSlot.Piece.Gloves, Armor.RoyalArcherTunic1)
        }
    )
    val orientations = sequencer(PawnSkeletonOrientation.all)
    val armorsSequencer by sequencer { armors }
    val weaponSequencer by sequencer { Sword.all }
    fun test() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            armorsSequencer.next {
                PawnArmorSlot.Piece.entries.forEach { slot ->
                    pawnPresenter.setArmor(slot, this)
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            armorsSequencer.prev {
                PawnArmorSlot.Piece.entries.forEach { slot ->
                    pawnPresenter.setArmor(slot, this)
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            weaponSequencer.next {
                pawnPresenter.setWeapon(PawnWeaponSlot.WeaponRight, this)
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            weaponSequencer.prev {
                pawnPresenter.setWeapon(PawnWeaponSlot.WeaponRight, this)
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            pawnPresenter.customize(CustomizablePawnSlotBody.Hair, Hair.CasualMessy1)
            pawnPresenter.customize(CustomizablePawnSlotBody.Beard, Beard.Type13)
            pawnPresenter.customize(CustomizablePawnSlotBody.Eyes, Eyes.Girl03)
            pawnPresenter.customize(CustomizablePawnSlotBody.Head, Body.Type7)
            pawnPresenter.customize(CustomizablePawnSlotBody.EyeBrows, Eyebrows.Eyebrows15)
            pawnPresenter.customize(CustomizablePawnSlotBody.Mouth, Mouth.Mouth09)
            pawnPresenter.customize(CustomizablePawnSlotBody.EarLeft, Ears.Type2)
            pawnPresenter.customize(CustomizablePawnSlotBody.EarRight, Ears.Type5)
            pawnPresenter.setWeapon(PawnWeaponSlot.WeaponRight, Dagger.BronzeDagger)
            pawnPresenter.setShield(Shield.Bloodmoon)
            pawnPresenter.setUnderwear(Underwear.FemaleUnderwearType1)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            pawnPresenter.strip()
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            pawnPresenter.slash1H()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            pawnPresenter.strip()
        }
    }

    override fun dispose() {
        weapons.forEach(EquipmentAtlas::dispose)
        armors.forEach(EquipmentAtlas::dispose)
        customizations.forEach(CustomizationAtlas::dispose)
        underwears.forEach(UnderWearAtlas::dispose)
        shields.forEach(ShieldAtlas::dispose)
        pawnPresenter.dispose()
    }
}