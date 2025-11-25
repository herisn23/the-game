package org.roldy.animation

import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Event


typealias EventListener<T> = AnimationState.TrackEntry.(T, Event)->Unit