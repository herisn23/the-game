package org.roldy.rendering.g2d.animation.skeleton

import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Event
import org.roldy.rendering.g2d.disposable.AutoDisposableAdapter

interface AnimationTypeEventListener<T : AnimationTypeEventListener<T>> {
    fun addEventListener(
        animationType: AnimationType,
        eventListener: EventListener<T>
    )
}

abstract class AnimationTypeEventListenerHandler<T : AnimationTypeEventListener<T>> : AutoDisposableAdapter(), AnimationTypeEventListener<T> {
    private val eventListeners = mutableMapOf<String, EventListener<T>>()
    override fun addEventListener(
        animationType: AnimationType,
        eventListener: EventListener<T>
    ) {
        eventListeners[animationType.name] = eventListener
    }

    fun AnimationState.TrackEntry.propagate(origin: T, event: Event) {
        eventListeners[animation.name]?.run {
            this(origin, event)
        }
    }
}