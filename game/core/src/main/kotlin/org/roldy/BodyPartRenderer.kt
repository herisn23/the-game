package org.roldy

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion

enum class BodyOrientation {
    FRONT, BACK, LEFT, RIGHT
}

data class BodyPartPosition(val offsetX: Float, val offsetY: Float)

class BodyPartRenderer(private val atlas: TextureAtlas) {
    private val bodyParts: Map<BodyOrientation, List<AtlasRegion>> = groupPartsByOrientation()
    private val partPositions: Map<String, BodyPartPosition> = createPartPositions()
    private val partTypeCache: Map<String, String> = createPartTypeCache()

    private fun groupPartsByOrientation(): Map<BodyOrientation, List<AtlasRegion>> {
        val allRegions = atlas.regions
        val leftRegions = allRegions.filter { it.name.startsWith("Left") }

        // Create flipped copies of LEFT regions for RIGHT orientation
        val rightRegions = leftRegions.map { region ->
            AtlasRegion(region).apply {
                flip(true, false) // Flip horizontally
            }
        }

        return mapOf(
            BodyOrientation.FRONT to allRegions.filter { it.name.startsWith("Front") },
            BodyOrientation.BACK to allRegions.filter { it.name.startsWith("Back") },
            BodyOrientation.LEFT to leftRegions,
            BodyOrientation.RIGHT to rightRegions
        )
    }

    private fun createPartPositions(): Map<String, BodyPartPosition> {
        // Define positions for each body part type
        // Positions are relative to a base point (bottom-center of the character)
        return mapOf(
            // Head positions (centered, at top)
            "Head" to BodyPartPosition(0f, 160f),

            // Body positions (centered, below head)
            "Body" to BodyPartPosition(0f, 0f),

            // Arm positions (on sides of body) - R/L swapped for correct positioning
            "ArmR" to BodyPartPosition(-80f, 0f),
            "ArmL" to BodyPartPosition(80f, 0f),

            // Leg positions (below body) - R/L swapped for correct positioning
            "LegR" to BodyPartPosition(-40f, -160f),
            "LegL" to BodyPartPosition(40f, -160f),

            // Hand positions (at end of arms) - R/L swapped for correct positioning
            "HandR" to BodyPartPosition(-80f, -80f),
            "HandL" to BodyPartPosition(80f, -80f),

            // Sleeve positions (same as arms) - R/L swapped for correct positioning
            "SleeveR" to BodyPartPosition(-80f, 80f),
            "SleeveL" to BodyPartPosition(80f, 80f),

            // Fingers positions (at end of hands) - R/L swapped for correct positioning
            "FingersR" to BodyPartPosition(-80f, -160f),
            "FingersL" to BodyPartPosition(80f, -160f)
        )
    }

    private fun createPartTypeCache(): Map<String, String> {
        // Cache part types for all regions to avoid string operations every frame
        return atlas.regions.associate { region ->
            region.name to region.name
                .removePrefix("Front")
                .removePrefix("Back")
                .removePrefix("Left")
        }
    }

    private fun getPartPosition(partName: String): BodyPartPosition {
        val partType = partTypeCache[partName] ?: return BodyPartPosition(0f, 0f)
        return partPositions[partType] ?: BodyPartPosition(0f, 0f)
    }

    /**
     * Renders all body parts for the specified orientation at the given position.
     * The x, y position represents the center-bottom of the character (where the body is anchored).
     */
    fun render(batch: SpriteBatch, orientation: BodyOrientation, x: Float, y: Float, scale: Float = 1f) =
        bodyParts[orientation]?.forEach { region ->

            val position = getPartPosition(region.name)
            val width = region.regionWidth * scale
            val height = region.regionHeight * scale

            // Calculate final position with offset
            // Center the parts horizontally by subtracting half width
            val finalX = x + (position.offsetX * scale) - (width / 2)
            val finalY = y + (position.offsetY * scale)

            batch.draw(region, finalX, finalY, width, height)
        }

    /**
     * Get a specific body part by orientation and part name (e.g., "Head", "Body", "ArmL")
     */
    fun getPart(orientation: BodyOrientation, partName: String): AtlasRegion? {
        val parts = bodyParts[orientation] ?: return null
        val prefix = when (orientation) {
            BodyOrientation.FRONT -> "Front"
            BodyOrientation.BACK -> "Back"
            BodyOrientation.LEFT, BodyOrientation.RIGHT -> "Left"
        }
        return parts.find { it.name == "$prefix$partName" }
    }

    /**
     * Get all parts for a specific orientation
     */
    fun getParts(orientation: BodyOrientation): List<AtlasRegion> {
        return bodyParts[orientation] ?: emptyList()
    }
}
