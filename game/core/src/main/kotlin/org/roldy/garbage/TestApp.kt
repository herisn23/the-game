package org.roldy.garbage

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import org.roldy.equipment.atlas.EquipmentAtlas
import org.roldy.equipment.atlas.armor.Armor
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.*
import org.roldy.equipment.atlas.weapon.Shield
import org.roldy.equipment.atlas.weapon.ShieldAtlas
import org.roldy.equipment.atlas.weapon.Wand
import org.roldy.equipment.atlas.weapon.Weapons
import org.roldy.listener.DefaultApplicationListener
import org.roldy.pawn.PawnRenderer
import org.roldy.pawn.skeleton.attribute.ArmorPawnSlot
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSkinSlot
import org.roldy.pawn.skeleton.attribute.WeaponPawnSlot
import org.roldy.utils.invoke

class TestApp(
    private val default: DefaultApplicationListener = DefaultApplicationListener()
) : ApplicationListener by default {
    private lateinit var pawnRenderer: PawnRenderer
    private lateinit var font: BitmapFont
    lateinit var testHair: CustomizationAtlas
    lateinit var testBeard: CustomizationAtlas
    lateinit var testEyes: CustomizationAtlas
    lateinit var testBody: CustomizationAtlas
    lateinit var testEyesBrows: CustomizationAtlas
    lateinit var testMouth: CustomizationAtlas
    lateinit var testEars: CustomizationAtlas
    lateinit var weapons: List<EquipmentAtlas>
    lateinit var armors: List<ArmorAtlas>

    lateinit var underwear: UnderWearAtlas
    lateinit var shield: ShieldAtlas

    override fun create() {
        armors = Armor.all
        weapons = Weapons.all

        testHair = Hair.Mohawk
        testBeard = Beard.Type4
        testEyes = Eyes.Evil
        testBody = Body.Type6
        testEyesBrows = Eyebrows.Eyebrows13
        testMouth = Mouth.CreepySmile
        testEars = Ears.Type10

        underwear = Underwear.FemaleUnderwearType2
        shield = Shield.CrusaderShield
        default.create()
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

    fun test() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            ArmorPawnSlot.allParts.forEach { slot ->
                pawnRenderer.setArmor(slot, armors.first())
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            pawnRenderer.customize(CustomizablePawnSkinSlot.Hair, testHair)
            pawnRenderer.customize(CustomizablePawnSkinSlot.Beard, testBeard)
            pawnRenderer.customize(CustomizablePawnSkinSlot.Eyes, testEyes)
            pawnRenderer.customize(CustomizablePawnSkinSlot.Head, testBody)
            pawnRenderer.customize(CustomizablePawnSkinSlot.EyeBrows, testEyesBrows)
            pawnRenderer.customize(CustomizablePawnSkinSlot.Mouth, testMouth)
            pawnRenderer.customize(CustomizablePawnSkinSlot.EarLeft, testEars)
            pawnRenderer.customize(CustomizablePawnSkinSlot.EarRight, testEars)
            pawnRenderer.setWeapon(WeaponPawnSlot.WeaponRight, Wand.BlackStick)
            pawnRenderer.setShield(shield)
            pawnRenderer.setUnderwear(underwear)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (pawnRenderer.skinColor == Color.BLUE) {
                pawnRenderer.skinColor = pawnRenderer.defaultSkinColor
            } else {
                pawnRenderer.skinColor = Color.BLUE
            }
            pawnRenderer.underwearColor = Color.GREEN
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            pawnRenderer.hairColor = Color.RED
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            pawnRenderer.strip()
        }
    }

    override fun dispose() {
        weapons.forEach(EquipmentAtlas::dispose)
        armors.forEach(EquipmentAtlas::dispose)
    }
}