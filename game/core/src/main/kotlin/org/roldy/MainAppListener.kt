package org.roldy

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class MainAppListener : ApplicationListener {

    lateinit var engine: Engine
    lateinit var batch: SpriteBatch
    lateinit var viewport: Viewport
    lateinit var atlas: TextureAtlas
    lateinit var parts: List<String>
    var partIndex = 0
    val sprite: Sprite by lazy {
        atlas.findRegion(parts[0])
            .let(::Sprite)
            .apply {
                setSize(regionWidth.toFloat(), regionHeight.toFloat())
            }
    }
    val desiredWidth = 480f
    val body:Sprite by lazy {
        atlas.findRegion(parts[4])
            .let(::Sprite)
            .apply {
                setSize(0.5f, 0.5f)
            }
    }
    val head:Sprite by lazy {
        atlas.findRegion(parts[0])
            .run {
                val sprite = Sprite(this)
                sprite.setSize(regionWidth.toFloat(), regionHeight.toFloat())
                sprite.setPosition(0f, 1f)
                sprite
            }
    }

    lateinit var bodyPartRenderer:BodyPartRenderer

    override fun create() {
        batch = SpriteBatch()
        parts = loadAsset("human.parts", BodyDestination).readString().split("\n")
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        atlas = TextureAtlas(loadAsset("human.atlas", BodyDestination))
        bodyPartRenderer = BodyPartRenderer(atlas)
        engine = Engine()
//        engine.addSystem(RenderSystem(batch, viewport))
//        engine.addEntity(Entity().apply {
//            val backgroundAtlas = TextureAtlas(loadAsset("test/atlas.atlas"))
//            val background by backgroundAtlas
//            add(TextureComponent(background))
//            add(PositionComponent())
//        })
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render() {
        changePart()
        ScreenUtils.clear(Color.BLACK)
        viewport.apply()
        batch.setProjectionMatrix(viewport.camera.combined)
        batch.begin()
//        sprite.draw(batch)
        bodyPartRenderer.render(batch, BodyOrientation.FRONT, 500f, 200f, 1f)
//        engine.update(Gdx.graphics.deltaTime)
//        head.draw(batch)
//        body.draw(batch)
        batch.end()
    }

    fun changePart() {
        val up = Gdx.input.isKeyJustPressed(Input.Keys.W)
        val down = Gdx.input.isKeyJustPressed(Input.Keys.S)
        if (up) {
            partIndex = (partIndex + 1) % parts.size
        }
        if (down) {
            partIndex = (partIndex - 1 + parts.size) % parts.size
        }
        if (up || down) {
            val region = atlas.findRegion(parts[partIndex])
            sprite.setRegion(region)
            sprite.setSize(region.regionWidth.toFloat(), region.regionHeight.toFloat())
//            val rw = region.regionWidth.toFloat()//80
//            val rh = region.regionHeight.toFloat()//160
//            val defaultW = 480
//            val defaultH = 480
//            val scale = rw / defaultW
//            fun calcSize(x: Float, y: Float) =
//                if (x < y) x / y else 1f
//            val h = calcSize(rh, rw)
//            val w = calcSize(rw, rh)
//            sprite.setSize(w, h)
        }

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }
}