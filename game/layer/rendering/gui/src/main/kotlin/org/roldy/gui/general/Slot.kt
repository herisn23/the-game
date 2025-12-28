package org.roldy.gui.general

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.roldy.core.utils.alpha
import org.roldy.gui.GuiContext
import org.roldy.gui.slotHover
import org.roldy.rendering.g2d.emptyImage
import org.roldy.rendering.g2d.gui.*
import org.roldy.rendering.g2d.gui.anim.*
import org.roldy.rendering.g2d.gui.el.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass

const val SlotSize = 126f

class SlotDragListener<T : Any>(
    val target: KClass<T>,
    val slot: Slot,
    val delegate: SlotDragDelegate<T>
) : DragListener(), AnimationDrawableStateResolver {
    var draggingIcon: UIImage? = null
    var prev: T? = null

    fun anim(drawable: Drawable): Drawable =
        mix(drawable) {
            add(::scale) {
                Normal to .8f
                Over to .8f
            }
            add(::alpha) {
                Normal to .8f
                Over to .8f
            }
        }

    override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int) {
        if (!delegate.canDrag()) return
        event.stop()
        // Create dragging icon
        draggingIcon = UIImage(anim(slot.icon.drawable)).apply {
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
        draggingIcon?.touchable = Touchable.disabled
        val hit = slot.root.stage.hit(event.stageX, event.stageY, true)

        val targetActor = hit?.traverseUserObject()
        if (targetActor != null) {
            state = Over
            delegate.onSlotEnter?.invoke(targetActor, prev)
        } else {
            draggingIcon?.touchable = Touchable.enabled
            state = Normal
        }
        if (prev != null && (targetActor == null || targetActor != prev)) {
            delegate.onSlotExit?.invoke(prev!!)
        }
        targetActor?.let {
            if (targetActor != prev) {
                prev = targetActor
            }
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

    override var state: AnimationDrawableState = Normal
}

@Scene2dCallbackDsl
class SlotDragDelegate<T> {
    var canDrag: () -> Boolean = { false }
    var onDrop: ((T) -> Unit)? = null
    var onEnd: (() -> Unit)? = null
    var onStart: ((Slot) -> Unit)? = null
    var onSlotEnter: ((actual: T, prev: T?) -> Unit)? = null
    var onSlotExit: ((actual: T) -> Unit)? = null
}


@Scene2dCallbackDsl
data class Slot(
    val root: UITable,
    private val button: UIImageButton,
    val icon: UIImage,
    private val additionalContent: UITable
) {

    fun onClick(onClick: (InputEvent) -> Unit) {
        button.onClick(onClick = onClick)
    }

    fun setIcon(drawable: Drawable?) {
        icon.drawable = drawable
    }

    fun content(cnt: UITable.() -> Unit) {
        additionalContent.cnt()
    }

    fun action(action: Action) {
        root.addAction(action)
    }

    fun inputListener(consuming: Boolean = true, listener: InputListenerProxy.() -> Unit) {
        button.inputListener(consuming, listener)
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
fun <S> UIWidget<S>.slot(
    init: (@Scene2dDsl Slot).(UITable) -> Unit = {}
): UITable =
    table(true) {
        val root = this
        image(gui.region { Slot_Background }) {
            touchable = Touchable.disabled
        }

        lateinit var icon: UIImage
        lateinit var content: UITable
        lateinit var button: UIImageButton

        table(true) {
            pad(8f)
            image(emptyImage(alpha(0f))) {
                touchable = Touchable.disabled
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
                drawable = slotHover { this },
                transition = {
                    Normal to 0f
                    Pressed to .4f
                    Over to 1f
                }
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