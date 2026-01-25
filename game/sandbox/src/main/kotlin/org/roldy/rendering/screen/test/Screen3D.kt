package org.roldy.rendering.screen.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import org.roldy.core.disposable.AutoDisposableScreenAdapter
import org.roldy.core.disposable.disposable
import org.roldy.core.utils.hex
import org.roldy.core.utils.sequencer
import org.roldy.g3d.pawn.PawnAnimations
import org.roldy.g3d.pawn.PawnAssetManager
import org.roldy.g3d.pawn.PawnConfiguration
import org.roldy.g3d.pawn.PawnShaderProvider2


class Screen3D(
    val camera: PerspectiveCamera
) : AutoDisposableScreenAdapter() {
    var loading = true

    init {
        camera.position.set(16.799997f, 0f, 0f)
        camera.lookAt(0f, 0f, 0f)
        camera.update()
    }

    fun Model.printModelData() {
        fun printBoneOrder(node: Node, indent: String = "") {
            if (node.id == "Root") return
            if (node.children.count() == 0) {
                val parents = mutableListOf<Node>()
                fun Node.parentPath() {
                    if (parent != null) {
                        parents.add(parent)
                        parent.parentPath()
                    }
                }
                node.parentPath()
                print(parents.reversed().joinToString("|") { it.id })
                println("|${node.id}")
            }

            node.children.forEach { printBoneOrder(it, "$indent  ") }
        }
        nodes.forEach { printBoneOrder(it) }
    }

    val character by lazy {
        PawnConfiguration().apply {
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
            setAnimation(character.instance.animations.first().id, -1)
        }
    }
    val batch by disposable { ModelBatch(PawnShaderProvider2(character)) }

    val light = DirectionalLight().set(hex("FFF4D6"), -1f, 1f, -0.2f)
    val env by lazy {
        Environment().apply {
            set(ColorAttribute(ColorAttribute.Emissive, hex("FFF4D6")))
            add(light)
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
        PawnAnimations[character.body].all.toList()
    }
    val colors by sequencer {
        listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
    }

    val allowed by sequencer {

        listOf(
            emptyList(),
            character.instance.allNodes.map { it.id },
            listOf(
                "PT_${character.body}_Armor_head_01",
                "PT_${character.body}_Armor_01_A_body",
                "PT_${character.body}_Armor_01_A_boots",
                "PT_${character.body}_Armor_01_A_cape",
                "PT_${character.body}_Armor_01_A_gauntlets",
                "PT_${character.body}_Armor_01_A_helmet",
                "PT_${character.body}_Armor_01_A_legs",
            ),
            listOf(
                "PT_${character.body}_Armor_head_01",
                "PT_${character.body}_Armor_01_A_body",
                "PT_${character.body}_Armor_01_A_boots",
                "PT_${character.body}_Armor_01_A_cape",
                "PT_${character.body}_Armor_01_A_gauntlets",
                "PT_${character.body}_Armor_Ex1_helmet_39",
                "PT_${character.body}_Armor_01_A_legs",
            ),
            listOf(
                "PT_${character.body}_Armor_Ex1_body_21",
                "PT_${character.body}_Armor_Ex1_boots_03",
                "PT_${character.body}_Armor_Ex1_cape_01",
                "PT_${character.body}_Armor_Ex1_gauntlets_02",
                "PT_${character.body}_Armor_Ex1_helmet_05",
                "PT_${character.body}_Armor_Ex1_legs_01",
            ),
            listOf(
                "PT_${character.body}_Armor_Ex1_body_21",
                "PT_${character.body}_Armor_Ex1_boots_03",
                "PT_${character.body}_Armor_Ex1_cape_01",
                "PT_${character.body}_Armor_Ex1_gauntlets_02",
                "PT_${character.body}_Armor_Ex1_helmet_33",
                "PT_${character.body}_Armor_Ex1_legs_01"
            )
        )
    }

    override fun render(delta: Float) {
        if (loading && PawnAssetManager.assetManager.update()) {
            loading = false

        }
        if (loading) return
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL)
        Gdx.gl.glViewport(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)


        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            character.leather3Color.set(colors.next())
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            character.skinColor.set(colors.next())
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            character.eyesColor.set(colors.next())
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            character.instance.setVisibility(
                allowed.next()
            )
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            light.direction.y += delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            light.direction.y -= delta
        }


        camera.update()

        controller.update()
        animController.update(delta)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            animController.setAnimation(anims.next().id, -1)
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