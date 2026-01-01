package org.roldy.rendering.g2d.gui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.roldy.core.utils.toSeconds
import kotlin.time.Duration

fun pulse(
    scaleTo: Float = 1.2f
): SequenceAction =
    Actions.sequence(
        Actions.scaleTo(scaleTo, scaleTo, 0.5f, Interpolation.smooth),
        Actions.scaleTo(1f, 1f, 0.5f, Interpolation.smooth)
    )


fun Action.forever(): Action =
    Actions.forever(this)

fun delay(duration: Duration, action: Runnable): DelayAction =
    Actions.delay(duration.toSeconds(), function(action))

fun function(action: Runnable): Action =
    Actions.run(action)

fun floatAction(
    from: Float,
    to: Float,
    duration: Duration,
    interpolation: Interpolation = Interpolation.linear,
    onUpdate: (Float) -> Unit
): Action {
    return object : TemporalAction(duration.toSeconds(), interpolation) {
        override fun update(percent: Float) {
            val value = from + (to - from) * percent
            onUpdate(value)
        }
    }
}
