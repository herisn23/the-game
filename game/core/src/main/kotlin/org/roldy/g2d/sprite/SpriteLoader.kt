package org.roldy.g2d.sprite

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.roldy.json.SpriteMetadata
import org.roldy.json.Vector
import org.roldy.g2d.atlas.AtlasAccessor
import org.roldy.g2d.atlas.LazyAtlasRegion
import org.roldy.g2d.atlas.region
import kotlin.reflect.KProperty

typealias SpriteMetadataAccessor = (String) -> SpriteMetadata
typealias SpriteInitializer = PivotalSprite.(TextureRegion) -> Unit

class SpriteData(
    internal val atlas: AtlasAccessor,
    metadata: List<SpriteMetadata>
) {
    val metadata = metadata.associateBy { it.name }
    operator fun get(name: String): SpriteMetadata = metadata.getValue(name)
}

class LazySprite(
    private val atlas: LazyAtlasRegion,
    private val spriteMetadata: SpriteMetadataAccessor,
    private val initializer: SpriteInitializer
) {
    private var spriteData: SpriteMetadata? = null
    private var sprite: PivotalSprite? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): PivotalSprite {
        val region = atlas.getValue(thisRef, property)
        val data = spriteData ?: spriteMetadata(property.name).also { spriteData = it }
        val pivot = data.metadata.pivot

        return sprite ?: PivotalSprite(region, pivot.toVector2()).apply {
            setSize(region.regionWidth.toFloat(), region.regionHeight.toFloat())
            setOrigin(pivot.x, pivot.y)
            initializer(region)
        }.also { sprite = it }
    }
}

fun sprite(atlas: AtlasAccessor, metadata: SpriteMetadataAccessor, initializer: SpriteInitializer = {}) =
    LazySprite(region(atlas), metadata, initializer)

fun sprite(spriteData: SpriteData, initializer: SpriteInitializer = {}) =
    sprite(spriteData.atlas, { spriteData[it] }, initializer)

fun Vector.toVector2() = Vector2(x, y)

