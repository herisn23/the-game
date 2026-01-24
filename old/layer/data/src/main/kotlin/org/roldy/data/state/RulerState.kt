package org.roldy.data.state

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Serializable
import org.roldy.data.configuration.G2DColorSerializer

@Serializable
class RulerState(
    @Serializable(G2DColorSerializer::class)
    var color: Color
) {
}