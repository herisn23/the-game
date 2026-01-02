package org.roldy.rendering.g2d.animation.skeleton

import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Event


typealias EventListener<T> = AnimationState.TrackEntry.(T, Event)->Unit