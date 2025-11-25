package org.roldy.garbage

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.BitmapFont
import org.roldy.equipment.atlas.EquipmentAtlas
import org.roldy.equipment.atlas.armor.Armor
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.*
import org.roldy.equipment.atlas.weapon.*
import org.roldy.listener.DefaultApplicationListener
import org.roldy.pawn.PawnRenderer
import org.roldy.pawn.skeleton.attribute.*
import org.roldy.utils.invoke
import org.roldy.utils.sequencer
import org.roldy.utils.toggle

class TestApp(
    private val default: DefaultApplicationListener = DefaultApplicationListener()
) : ApplicationListener by default {
    private lateinit var pawnRenderer: PawnRenderer
    private lateinit var font: BitmapFont

    lateinit var weapons: List<EquipmentAtlas>
    lateinit var armors: List<ArmorAtlas>
    lateinit var customizations: List<CustomizationAtlas>
    lateinit var underwears: List<UnderWearAtlas>
    lateinit var shields: List<ShieldAtlas>

    override fun create() {
        default.create()

        armors = Armor.all
        weapons = Weapons.all
        customizations = CustomizationAtlas.all
        underwears = Underwear.all
        shields = Shield.all

        pawnRenderer = PawnRenderer()
        font = BitmapFont()
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        default.render()
        test()
        default.batch {
            context(delta, this) {
                pawnRenderer.render()
            }

            font.draw(default.batch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, Gdx.graphics.height - 10f)
            font.draw(default.batch, "Delta: ${Gdx.graphics.deltaTime}", 10f, Gdx.graphics.height - 30f)
            font.draw(default.batch, "Memory: ${Gdx.app.javaHeap / 1024 / 1024} MB", 10f, Gdx.graphics.height - 50f)
        }
    }

    var toggleGloves = toggle(
        onTrue = {
            pawnRenderer.setArmor(ArmorPawnSlot.Piece.Gloves, Armor.NordicHunterArmorHeavy1)
        },
        onFalse = {
            pawnRenderer.setArmor(ArmorPawnSlot.Piece.Gloves, Armor.RoyalArcherTunic1)
        }
    )
    val orientations = sequencer(PawnSkeletonOrientation.all)
    val armorsSequencer by sequencer { armors }
    val weaponSequencer by sequencer { Sword.all }
    fun test() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            armorsSequencer.next {
                ArmorPawnSlot.Piece.entries.forEach { slot ->
                    pawnRenderer.setArmor(slot, this)
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            armorsSequencer.prev {
                ArmorPawnSlot.Piece.entries.forEach { slot ->
                    pawnRenderer.setArmor(slot, this)
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            weaponSequencer.next {
                pawnRenderer.setWeapon(WeaponPawnSlot.WeaponRight, this)
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            weaponSequencer.prev {
                pawnRenderer.setWeapon(WeaponPawnSlot.WeaponRight, this)
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            pawnRenderer.customize(CustomizablePawnSkinSlot.Hair, Hair.CasualMessy1)
            pawnRenderer.customize(CustomizablePawnSkinSlot.Beard, Beard.Type13)
            pawnRenderer.customize(CustomizablePawnSkinSlot.Eyes, Eyes.Girl03)
            pawnRenderer.customize(CustomizablePawnSkinSlot.Head, Body.Type7)
            pawnRenderer.customize(CustomizablePawnSkinSlot.EyeBrows, Eyebrows.Eyebrows15)
            pawnRenderer.customize(CustomizablePawnSkinSlot.Mouth, Mouth.Mouth09)
            pawnRenderer.customize(CustomizablePawnSkinSlot.EarLeft, Ears.Type2)
            pawnRenderer.customize(CustomizablePawnSkinSlot.EarRight, Ears.Type5)
            pawnRenderer.setWeapon(WeaponPawnSlot.WeaponRight, Dagger.BronzeDagger)
            pawnRenderer.setShield(Shield.Bloodmoon)
            pawnRenderer.setUnderwear(Underwear.FemaleUnderwearType1)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            pawnRenderer.strip()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            pawnRenderer.currentOrientation = Back
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            pawnRenderer.currentOrientation = Left
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            pawnRenderer.currentOrientation = Right
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            pawnRenderer.currentOrientation = Front
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            pawnRenderer.slash1H()
        }
    }

    override fun dispose() {
        weapons.forEach(EquipmentAtlas::dispose)
        armors.forEach(EquipmentAtlas::dispose)
        customizations.forEach(CustomizationAtlas::dispose)
        underwears.forEach(UnderWearAtlas::dispose)
        shields.forEach(ShieldAtlas::dispose)
        pawnRenderer.dispose()
    }
}