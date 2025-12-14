package org.roldy.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import org.roldy.core.asset.AtlasLoader
import org.roldy.rendering.g2d.disposable.AutoDisposable
import org.roldy.rendering.g2d.disposable.disposable


data class GUIColors(
    val default: Color,
    val tint: Color,
)

fun AutoDisposable.gui(stack: context(TextureAtlas, GUIColors) () -> Actor): Stage {
    val stage by disposable { Stage() }
    val atlas by disposable { AtlasLoader.gui }
    val colors = GUIColors(
        default = Color.RED,
        tint = Color.valueOf("FFEDCFFF")
    )
    stage.addActor(stack(atlas, colors))
    return stage
}