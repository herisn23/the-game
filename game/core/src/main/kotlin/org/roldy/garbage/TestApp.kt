package org.roldy.garbage

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.roldy.asset.loadAsset
import org.roldy.g2d.sprite.invoke
import org.roldy.listener.DefaultApplicationListener
import org.roldy.pawn.Pawn
import org.roldy.pawn.skeleton.attribute.ArmorPawnSlot
import org.roldy.pawn.skeleton.attribute.CustomizablePawnSkinSlot

class TestApp(
    private val default: DefaultApplicationListener = DefaultApplicationListener()
) : ApplicationListener by default {
    private lateinit var pawn: Pawn
    private lateinit var font: BitmapFont
    lateinit var testArmor: TextureAtlas
    lateinit var testHair: TextureAtlas
    lateinit var testBeard: TextureAtlas

    override fun create() {
        testArmor = TextureAtlas(loadAsset("pawn/human/armor/AcornArmor [ShowEars].atlas"))
        testHair = TextureAtlas(loadAsset("pawn/human/customization/hair/BroFlow.atlas"))
        testBeard = TextureAtlas(loadAsset("pawn/human/customization/beard/Type10.atlas"))
        default.create()
        pawn = Pawn()
        font = BitmapFont()
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        default.render()
        test()
        default.batch {
            context(delta, this) {
                pawn.render()
            }
            font.draw(default.batch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, Gdx.graphics.height - 10f)
            font.draw(default.batch, "Delta: ${Gdx.graphics.deltaTime}", 10f, Gdx.graphics.height - 30f)
            font.draw(default.batch, "Memory: ${Gdx.app.javaHeap / 1024 / 1024} MB", 10f, Gdx.graphics.height - 50f)
        }
    }

    fun test() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            ArmorPawnSlot.allParts.forEach { slot ->
                pawn.setArmor(slot, testArmor)
            }
//            ArmorPawnSlot.pieces[Piece.Legs]?.forEach { slot->
//                pawn.setArmor(slot, testArmor)
//            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            pawn.customize(CustomizablePawnSkinSlot.Hair, testHair)
            pawn.customize(CustomizablePawnSkinSlot.Beard, testBeard)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            pawn.skinColor = Color.BLUE
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            pawn.hairColor = Color.RED
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            pawn.strip()
        }
    }
}