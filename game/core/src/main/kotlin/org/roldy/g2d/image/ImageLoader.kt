package org.roldy.g2d.image

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import org.roldy.g2d.atlas.AtlasAccessor
import org.roldy.g2d.atlas.LazyAtlasRegion
import org.roldy.g2d.atlas.region
import org.roldy.g2d.sprite.SpriteData
import org.roldy.g2d.sprite.toVector2
import org.roldy.json.SpriteMetadata
import kotlin.reflect.KProperty

typealias ImageMetadataAccessor = (String) -> SpriteMetadata
typealias ImageInitializer = PivotalImage.(TextureRegion) -> Unit

class LazyImage(
    private val atlas: LazyAtlasRegion,
    private val spriteMetadata: ImageMetadataAccessor,
    private val initializer: ImageInitializer
) {
    private var spriteData: SpriteMetadata? = null
    private var image: PivotalImage? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): PivotalImage {
        val region = atlas.getValue(thisRef, property)
        val data = spriteData ?: spriteMetadata(property.name).also { spriteData = it }
        val pivot = data.metadata.pivot

        return image ?: PivotalImage(region, pivot.toVector2()).apply {
            setSize(region.regionWidth.toFloat(), region.regionHeight.toFloat())
            setOrigin(width * pivot.x, height * pivot.y)
            initializer(region)
        }.also { image = it }
    }
}

fun image(atlas: AtlasAccessor, metadata: ImageMetadataAccessor, initializer: ImageInitializer = {}) =
    LazyImage(region(atlas), metadata, initializer)

fun image(spriteData: SpriteData, initializer: ImageInitializer = {}) =
    image(spriteData.atlas, { spriteData[it] }, initializer)

