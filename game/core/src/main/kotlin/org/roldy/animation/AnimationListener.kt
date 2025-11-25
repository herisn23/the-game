package org.roldy.animation

import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Event


infix fun AnimationState.add(listener: AnimationState.AnimationStateAdapter) {
    addListener(listener)
}


fun listener(
    start: AnimationState.TrackEntry.() -> Unit = {},
    interrupt: AnimationState.TrackEntry.() -> Unit = {},
    end: AnimationState.TrackEntry.() -> Unit = {},
    dispose: AnimationState.TrackEntry.() -> Unit = {},
    complete: AnimationState.TrackEntry.() -> Unit = {},
    event: AnimationState.TrackEntry.(Event) -> Unit = {},
) = object : AnimationState.AnimationStateAdapter() {
    override fun complete(entry: AnimationState.TrackEntry) {
        entry.complete()
    }

    override fun start(entry: AnimationState.TrackEntry) {
        entry.start()
    }

    override fun end(entry: AnimationState.TrackEntry) {
        entry.end()
    }

    override fun interrupt(entry: AnimationState.TrackEntry) {
        entry.interrupt()
    }

    override fun dispose(entry: AnimationState.TrackEntry) {
        entry.dispose()
    }

    override fun event(entry: AnimationState.TrackEntry, event: Event) {
        entry.event(event)
    }
}