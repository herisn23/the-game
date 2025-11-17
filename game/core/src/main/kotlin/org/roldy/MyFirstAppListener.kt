package org.roldy

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import org.roldy.coroutines.async
import java.lang.Thread.sleep
import kotlin.reflect.KProperty


class Main : ApplicationListener {
    //    var backgroundTexture: Texture? = null
//    var bucketTexture: Texture? = null
//    var dropTexture: Texture? = null
    var dropSound: Sound? = null
    var music: Music? = null
    var spriteBatch: SpriteBatch? = null
    var viewport: FitViewport? = null
    var bucketSprite: Sprite? = null
    var touchPos: Vector2? = null
    var dropSprites: Array<Sprite>? = null
    var dropTimer: Float = 0f
    var bucketRectangle: Rectangle? = null
    var dropRectangle: Rectangle? = null
    lateinit var atlas: TextureAtlas

    val background by { atlas }
    val drop by { atlas }
    val bucket by { atlas }

    override fun create() {
        atlas = TextureAtlas(loadAsset("test/atlas.atlas"))
        dropSound = Gdx.audio.newSound(loadAsset("drop.mp3"))
        music = Gdx.audio.newMusic(loadAsset("music.mp3"))
        spriteBatch = SpriteBatch()
        viewport = FitViewport(8f, 5f)
        bucketSprite = Sprite(bucket)
        bucketSprite!!.setSize(1f, 1f)
        touchPos = Vector2()
        dropSprites = Array<Sprite>()
        bucketRectangle = Rectangle()
        dropRectangle = Rectangle()
        music!!.setLooping(true)
        music!!.setVolume(.5f)
        music!!.play()
    }

    override fun resize(width: Int, height: Int) {
        viewport!!.update(width, height, true)
    }

    override fun render() {
        input()
        logic()
        draw()
    }

    var processing = false

    private fun input() {
        val speed = 4f
        val delta = Gdx.graphics.deltaTime

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite!!.translateX(speed * delta)
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite!!.translateX(-speed * delta)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && !processing) {
            async { onRender ->
                onRender {
                    Gdx.graphics.setTitle("hovinko")
                }
            }
        }

        if (Gdx.input.isTouched) {
            touchPos!!.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            viewport!!.unproject(touchPos)
            bucketSprite!!.setCenterX(touchPos!!.x)
        }
    }

    private fun logic() {
        val worldWidth = viewport!!.getWorldWidth()
        val worldHeight = viewport!!.getWorldHeight()
        val bucketWidth = bucketSprite!!.getWidth()
        val bucketHeight = bucketSprite!!.getHeight()

        bucketSprite!!.setX(MathUtils.clamp(bucketSprite!!.getX(), 0f, worldWidth - bucketWidth))

        val delta = Gdx.graphics.deltaTime
        bucketRectangle!!.set(bucketSprite!!.getX(), bucketSprite!!.getY(), bucketWidth, bucketHeight)

        for (i in dropSprites!!.size - 1 downTo 0) {
            val dropSprite = dropSprites!!.get(i)
            val dropWidth = dropSprite.getWidth()
            val dropHeight = dropSprite.getHeight()

            dropSprite.translateY(-2f * delta)
            dropRectangle!!.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight)

            if (dropSprite.getY() < -dropHeight) dropSprites!!.removeIndex(i)
            else if (bucketRectangle!!.overlaps(dropRectangle)) {
                dropSprites!!.removeIndex(i)
                dropSound!!.play()
            }
        }

        dropTimer += delta
        if (dropTimer > 1f) {
            dropTimer = 0f
            createDroplet()
        }
    }

    private fun draw() {
        ScreenUtils.clear(Color.BLACK)
        viewport!!.apply()
        spriteBatch!!.setProjectionMatrix(viewport!!.getCamera().combined)
        spriteBatch!!.begin()

        val worldWidth = viewport!!.getWorldWidth()
        val worldHeight = viewport!!.getWorldHeight()

        spriteBatch!!.draw(background, 0f, 0f, worldWidth, worldHeight)
//        bucketSprite!!.draw(spriteBatch)
//
//        for (dropSprite in dropSprites!!) {
//            dropSprite.draw(spriteBatch)
//        }

        spriteBatch!!.end()
    }

    private fun createDroplet() {
        val dropWidth = 1f
        val dropHeight = 1f
        val worldWidth = viewport!!.getWorldWidth()
        val worldHeight = viewport!!.getWorldHeight()

        val dropSprite = Sprite(drop)
        dropSprite.setSize(dropWidth, dropHeight)
        dropSprite.setX(MathUtils.random(0f, worldWidth))
        dropSprite.setY(worldHeight)
        dropSprites!!.add(dropSprite)
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {

    }
}