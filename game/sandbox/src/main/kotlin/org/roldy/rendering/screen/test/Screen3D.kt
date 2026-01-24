package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.roldy.core.disposable.AutoDisposableScreenAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.utils.sequencer


class Screen3D(
    val camera: PerspectiveCamera
) : AutoDisposableScreenAdapter() {
    var loading = true
    val assetManager = AssetManager().apply {
        load("3d/ModularCharacters.g3db", Model::class.java)
        load("3d/Idle.g3db", Model::class.java)
        load("3d/Idle2.g3db", Model::class.java)
        load("3d/Walking.g3db", Model::class.java)
        load("3d/PolygonFantasyHero_Texture_01_A.png", Texture::class.java)
        load("3d/PolygonFantasyHero_Texture_Mask_01.png", Texture::class.java)
        load("3d/PolygonFantasyHero_Texture_Mask_02.png", Texture::class.java)
        load("3d/PolygonFantasyHero_Texture_Mask_03.png", Texture::class.java)
        load("3d/PolygonFantasyHero_Texture_Mask_04.png", Texture::class.java)
        load("3d/PolygonFantasyHero_Texture_Mask_05.png", Texture::class.java)
    }

    init {
        camera.position.set(16.799997f, 0f, 0f)
        camera.lookAt(0f, 0f, 0f)
        camera.update()
    }

    val maskTextures by lazy {
        fun setupTexture(path: String, flip: Boolean = false): Texture {
            val tex = assetManager.get<Texture>(path)
            if (flip) {
                // Flip texture vertically
                if (!tex.textureData.isPrepared) {
                    tex.textureData.prepare()
                }
                val pixmap = tex.textureData.consumePixmap()

                // Flip Y
                val flipped = Pixmap(pixmap.width, pixmap.height, pixmap.format)
                for (y in 0 until pixmap.height) {
                    for (x in 0 until pixmap.width) {
                        flipped.drawPixel(x, pixmap.height - 1 - y, pixmap.getPixel(x, y))
                    }
                }

                val flippedTex = Texture(flipped)
                flippedTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
                flippedTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

                pixmap.dispose()
                flipped.dispose()
                return flippedTex
            }
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
            tex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
            return tex
        }

        val flip = true
        MaskTextures(
            baseTexture = setupTexture("3d/PolygonFantasyHero_Texture_01_A.png", flip = flip),
            mask01 = setupTexture("3d/PolygonFantasyHero_Texture_Mask_01.png", flip = flip),
            mask02 = setupTexture("3d/PolygonFantasyHero_Texture_Mask_02.png", flip = flip),
            mask03 = setupTexture("3d/PolygonFantasyHero_Texture_Mask_03.png", flip = flip),
            mask04 = setupTexture("3d/PolygonFantasyHero_Texture_Mask_04.png", flip = flip),
            mask05 = setupTexture("3d/PolygonFantasyHero_Texture_Mask_05.png", flip = flip)
        )
    }
    val idle by lazy {
        assetManager.get("3d/Idle.g3db", Model::class.java)
    }
    val idle2 by lazy {
        assetManager.get("3d/Idle2.g3db", Model::class.java)
    }
    val walking by lazy {
        assetManager.get("3d/Walking.g3db", Model::class.java)
    }
    val orig by lazy {
        assetManager.get<Model>("3d/ModularCharacters.g3db").apply {
            printModelData()
        }
    }

    fun Model.printModelData() {
        // Print the node structure to see bone ordering
        fun printBoneOrder(node: Node, indent: String = "") {
            println("$indent${node.id}")
            node.children.forEach { printBoneOrder(it, "$indent  ") }
        }

        println("=== Model Node Hierarchy ===")
        nodes.forEach { printBoneOrder(it) }
    }

    val character by lazy {
        CharacterController(
            orig, maskTextures,
            mapOf(
                "idle" to idle.animations.first(),
                "idle2" to idle2.animations.first(),
                "walking" to walking.animations.first()
            )
        ).apply {
            val initialTransform = Matrix4()
            initialTransform.idt()
            initialTransform.scl(0.1f)
            initialTransform.setTranslation(0f, -9.700001f, 0f)
            initialTransform.rotate(Vector3.Y, 90f)
            instance.transform.set(initialTransform) // Copy it over
        }
    }
    val animController by lazy {
        AnimationController(character.instance).apply {
            setAnimation("idle", -1)
        }
    }
    val batch by disposable { ModelBatch(CharacterShaderProvider(character, camera)) }


    val env by lazy {
        Environment().apply {
            set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
            add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        }
    }

    fun printNodeHierarchy(node: Node, indent: String = "") {
        println("$indent${node.id}")
        for (child in node.children) {
            printNodeHierarchy(child, "$indent  ")
        }
    }

    val controller by lazy {
        ModelController(character.instance, camera).also(Gdx.input::setInputProcessor).apply {

        }
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.update()
    }

    val anims by sequencer {
        listOf("idle", "idle2", "walking")
    }
    val colors by sequencer {
        listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
    }
    val parts by sequencer {
        listOf(
            character.colorPrimary,
            character.colorSecondary,
            character.colorLeatherPrimary,
            character.colorLeatherSecondary,
            character.colorMetalPrimary,
            character.colorMetalSecondary,
            character.colorMetalDark,
            character.colorHair,
            character.colorSkin,
            character.colorStubble,
            character.colorScar,
            character.colorBodyArt,
            character.colorEyes
        )
    }
    val rotation = Quaternion()
    override fun render(delta: Float) {
        if (loading && assetManager.update()) {
            loading = false

        }
        if (loading) return
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)
        Gdx.gl.glViewport(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight)
        Gdx.gl.glClearColor(1f, 0f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            parts.next()
            println(parts.current)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            character.colorBodyArt.set(colors.next())
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            character.colorSkin.set(colors.next())
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            character.colorEyes.set(colors.next())
        }


        camera.update()

        controller.update()
        animController.update(delta)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            animController.setAnimation(anims.next(), -1)
        }

        batch.begin(camera)
        batch.render(character.instance, env)
        batch.end()
    }

}

class ModelController(
    val model: ModelInstance,
    val camera: PerspectiveCamera
) : InputAdapter() {
    var position: Vector3 = model.transform.getTranslation(Vector3())
    private var zoom = 10f // Distance from target
    var lastKey = -1
    override fun keyDown(keycode: Int): Boolean {
        lastKey = keycode
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        lastKey = -1
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        // Zoom in/out
        zoom += amountY * 2f // Adjust multiplier for zoom speed
        zoom = zoom.coerceIn(2f, 500f) // Clamp between min and max distance
        updateCameraPosition()
        return true
    }

    private fun updateCameraPosition() {
        // Move camera along its current direction
        val direction = Vector3(camera.direction).nor()
        camera.position.set(0f, 0f, 0f).add(direction.scl(-zoom))
        camera.lookAt(0f, 0f, 0f)
        camera.update()

    }

    fun update() {
        when (lastKey) {
            Input.Keys.W -> {
                position.y += 0.1f
            }

            Input.Keys.S -> {
                position.y -= 0.1f
            }

            Input.Keys.Q -> {
                model.transform.rotate(Vector3.Y, 1f)
            }

            Input.Keys.E -> {
                model.transform.rotate(Vector3.Y, -1f)
            }
        }
//        model.transform.setTranslation(position)
    }
}