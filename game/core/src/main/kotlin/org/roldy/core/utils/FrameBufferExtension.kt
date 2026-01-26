package org.roldy.core.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.FrameBuffer


fun FrameBuffer.capture(render: () -> Unit) =
    apply {
        begin()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        render()
        end()
    }

operator fun FrameBuffer.invoke(render: () -> Unit) = capture(render)