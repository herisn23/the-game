package org.roldy.garbage

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import org.roldy.asset.loadAsset
import org.roldy.equipment.atlas.EquipmentAtlas
import org.roldy.equipment.atlas.armor.Armor
import org.roldy.equipment.atlas.armor.ArmorAtlas
import org.roldy.equipment.atlas.customization.*
import org.roldy.equipment.atlas.weapon.*
import org.roldy.listener.DefaultApplicationListener
import org.roldy.pawn.skeleton.PawnSkeletonManager
import org.roldy.pawn.skeleton.attribute.*
import org.roldy.utils.invoke
import org.roldy.utils.sequencer
import org.roldy.utils.toggle

class TestApp(
    private val default: DefaultApplicationListener = DefaultApplicationListener()
) : ApplicationListener by default {
    private lateinit var pawnManager: PawnSkeletonManager
    private lateinit var font: BitmapFont

    lateinit var weapons: List<EquipmentAtlas>
    lateinit var armors: List<ArmorAtlas>
    lateinit var customizations: List<CustomizationAtlas>
    lateinit var underwears: List<UnderWearAtlas>
    lateinit var shields: List<ShieldAtlas>
    lateinit var bg: Sprite

    lateinit var uiCamera: OrthographicCamera
    lateinit var uiViewport: FitViewport

    class MyInputProcessor(val app: TestApp) : InputAdapter() {
        var lastKeycode: Int = 0
        val speed = 5f
        val keyActions = mapOf(
            Input.Keys.W to (
                    {
                        app.pawnManager.currentOrientation = Back
                        app.pawnManager.walk(speed)
                    } to {
                        app.pawnManager.stop()
                    }),
            Input.Keys.A to (
                    {
                        app.pawnManager.currentOrientation = Left
                        app.pawnManager.walk(speed)
                    } to {
                        app.pawnManager.stop()
                    }),
            Input.Keys.S to (
                    {
                        app.pawnManager.currentOrientation = Front
                        app.pawnManager.walk(speed)
                    } to {
                        app.pawnManager.stop()
                    }),
            Input.Keys.D to (
                    {
                        app.pawnManager.currentOrientation = Right
                        app.pawnManager.walk(speed)
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

    override fun resize(width: Int, height: Int) {
        default.resize(width, height)
        uiViewport.update(width, height, true)
    }

    override fun create() {
        default.create()
        default.camera.zoom = 3f
        uiCamera = OrthographicCamera()
        uiViewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), uiCamera)
        bg = Sprite(Texture(loadAsset("test-bg.jpg")))
        Gdx.input.inputProcessor = MyInputProcessor(this)
        armors = Armor.all
        weapons = Weapons.all
        customizations = CustomizationAtlas.all
        underwears = Underwear.all
        shields = Shield.all

        pawnManager = PawnSkeletonManager().apply {
            addEventListener(Slash1H) { _, _ ->
                println("hit")
            }
        }
        font = BitmapFont()
    }

    override fun render() {
        default.camera.position.set(pawnManager.x, pawnManager.y, 0f)
        default.camera.update()
        val delta = Gdx.graphics.deltaTime
        default.render()
        test()
        default.batch {
            context(delta, this) {
                bg.draw(this)
                pawnManager.render()
            }
        }
        default.batch.projectionMatrix = uiCamera.combined
        default.batch {
            font.draw(this, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, Gdx.graphics.height - 10f)
            font.draw(this, "Delta: ${Gdx.graphics.deltaTime}", 10f, Gdx.graphics.height - 30f)
            font.draw(this, "Memory: ${Gdx.app.javaHeap / 1024 / 1024} MB", 10f, Gdx.graphics.height - 50f)
        }
    }

    var toggleGloves = toggle(
        onTrue = {
            pawnManager.setArmor(PawnArmorSlot.Piece.Gloves, Armor.NordicHunterArmorHeavy1)
        },
        onFalse = {
            pawnManager.setArmor(PawnArmorSlot.Piece.Gloves, Armor.RoyalArcherTunic1)
        }
    )
    val orientations = sequencer(PawnSkeletonOrientation.all)
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            pawnManager.strip()
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            pawnManager.slash1H(1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
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
    }
}