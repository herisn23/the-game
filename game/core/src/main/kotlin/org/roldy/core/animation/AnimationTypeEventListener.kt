package org.roldy.core.animation

import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Event

interface AnimationTypeEventListener<T : AnimationTypeEventListener<T>> {
    fun addEventListener(
        animationType: AnimationType,
        eventListener: EventListener<T>
    )
}

abstract class AnimationTypeEventListenerHandler<T : AnimationTypeEventListener<T>> : AnimationTypeEventListener<T> {
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