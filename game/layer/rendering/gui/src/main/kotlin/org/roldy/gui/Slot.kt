package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import org.roldy.core.utils.alpha
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass

const val SlotSize = 126f

class SlotDragListener<T : Any>(
    val target: KClass<T>,
    val slot: Slot,
    val delegate: SlotDragDelegate<T>
) : DragListener() {
    var draggingIcon: KImage? = null
    fun drawable(drawable: Drawable): Drawable {
        return when (drawable) {
            is TextureRegionDrawable -> TextureRegionDrawable(drawable.region)
            is NinePatchDrawable -> NinePatchDrawable(drawable.patch)
            else -> drawable
        }
    }

    override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int) {
        if (!delegate.canDrag()) return
        event.stop()
        // Create dragging icon
        draggingIcon = KImage(drawable(slot.icon.drawable)).apply {
            touchable = Touchable.enabled
            setSize(slot.icon.width, slot.icon.height)
            color = slot.icon.color alpha 1f
        }

        slot.root.stage.addActor(draggingIcon)

        // Position at cursor using STAGE coordinates
        draggingIcon?.setPosition(
            event.stageX - slot.icon.width / 2,
            event.stageY - slot.icon.height / 2
        )
        delegate.onStart?.invoke(slot)
    }

    override fun drag(event: InputEvent, x: Float, y: Float, pointer: Int) {
        if (!delegate.canDrag()) return
        event.stop()
        val hit = slot.root.stage.hit(event.stageX, event.stageY, true)

        val targetActor = hit.traverseUserObject()
        targetActor?.let {
            delegate.onSlotEnter?.invoke(it)
        }
        // Use STAGE coordinates
        draggingIcon?.setPosition(
            event.stageX - slot.icon.width / 2,
            event.stageY - slot.icon.height / 2
        )
    }

    override fun dragStop(event: InputEvent, x: Float, y: Float, pointer: Int) {
        if (!delegate.canDrag()) return
        event.stop()
        draggingIcon?.remove()
        draggingIcon = null

        // Find actor at drop position
        val targetActor = slot.root.stage.hit(event.stageX, event.stageY, true)
        delegate.onEnd?.invoke()
        targetActor?.traverseUserObject()?.let {
            delegate.onDrop?.invoke(it)
        }
        if (targetActor != null && targetActor != draggingIcon) {
            println("Dropped on: ${targetActor::class.simpleName}")

        } else {
            println("Dropped on empty space")
        }

    }

    fun Actor.traverseActor(): Actor? =
        if (userObject != null && userObject::class == target) {
            this
        } else if (parent != null) {
            parent!!.traverseActor()
        } else {
            null
        }

    fun Actor.traverseUserObject(): T? =
        traverseActor()?.run {
            userObject as T
        }
}

@Scene2dCallbackDsl
class SlotDragDelegate<T> {
    var canDrag: () -> Boolean = { false }
    var onDrop: ((T) -> Unit)? = null
    var onEnd: (() -> Unit)? = null
    var onStart: ((Slot) -> Unit)? = null
    var onSlotEnter: ((T) -> Unit)? = null
}


@Scene2dCallbackDsl
data class Slot(
    val root: KTable,
    private val button: KImageButton,
    val icon: KImage,
    private val additionalContent: KTable
) {

    fun onClick(onClick: () -> Unit) {
        button.onClick(onClick)
    }

    fun setIcon(drawable: Drawable?) {
        icon.drawable = drawable
    }

    fun content(cnt: KTable.() -> Unit) {
        additionalContent.cnt()
    }

    var isDisabled by Delegates.observable(false) { _, _, newValue ->
        button.isDisabled = newValue
        button.touchable = if (!newValue) Touchable.enabled else Touchable.disabled
    }

    inline fun <reified T : Any> draggable(
        delegate: SlotDragDelegate<T>.() -> Unit
    ) {
        root.addListener(
            SlotDragListener(T::class, this, SlotDragDelegate<T>().apply {
                delegate()
            })
        )
    }
}

@Scene2dDsl
context(gui: GuiContext)
fun <S> KWidget<S>.slot(
    init: (@Scene2dDsl Slot).(KTable) -> Unit = {}
): KTable =
    table(true) {
        val root = this
        image(gui.region { Slot_Background })

        lateinit var icon: KImage
        lateinit var content: KTable
        lateinit var button: KImageButton

        table(true) {
            pad(8f)
            image(emptyImage(alpha(0f))) {
                icon = this
            }
        }
        table {
            pad(8f)
            content = this
        }
        table(true) {
            pad(3f)

            imageButton(
                buttonDrawable(
                    drawable = slotHover { this },
                    transition = transition(
                        normalColor = Color.WHITE alpha 0f,
                        pressedColor = Color.WHITE alpha .4f,
                        overColor = Color.WHITE alpha 1f,
                    )
                )
            ) {
                button = this
                Slot(root, button, icon, content).init(root)
            }
        }
        table(true) {
            touchable = Touchable.disabled
            image(
                emptyImage(Color.RED alpha .1f) redraw { x, y, width, height, draw ->
                    if (button.isDisabled) {
                        draw(x, y, width, height)
                    }
                })
        }
        table(true) {
            pad(-9f)
            image(gui.region { Slot_Border }) {
                touchable = Touchable.disabled
            }
        }
    }