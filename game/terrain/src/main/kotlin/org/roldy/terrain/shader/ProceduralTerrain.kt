package org.roldy.terrain.shader
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils

class ProceduralTerrain {
    val shader: ShaderProgram

    private val albedoAtlas: TextureAtlas
    private val normalAtlas: TextureAtlas

    // Current terrain composition (4 terrain types blended)
    data class TerrainComposition(
        val terrain1: String,
        val terrain2: String,
        val terrain3: String,
        val terrain4: String
    )

    private var composition = TerrainComposition(
        terrain1 = "Grass",
        terrain2 = "Forest_Ground",
        terrain3 = "Rocky_Dirt",
        terrain4 = "Sandy_Rock_Surface"
    )

    private val blendMap: Texture
    var tileScale = 2f
    var debugMode:Int = 0

    init {
        shader = loadShader("shader/terrain_blend.vert", "shader/terrain_blend.frag")

        // Load your atlases
        albedoAtlas = TextureAtlas("terrain/Albedo.atlas")
        normalAtlas = TextureAtlas("terrain/Normal.atlas")

        // Generate procedural blend map
        blendMap = generateTerrainBlendMap(16024, 16024)
        ShaderProgram.pedantic = false
    }

    fun bind() {
        var textureUnit = 0

        // Bind blend map
        blendMap.bind(textureUnit)
        shader.setUniformi("u_blendMap", textureUnit++)

        // Bind atlases
        albedoAtlas.textures.first().bind(textureUnit)
        shader.setUniformi("u_albedoAtlas", textureUnit++)

        normalAtlas.textures.first().bind(textureUnit)
        shader.setUniformi("u_normalAtlas", textureUnit++)

        // Get regions and set UV coordinates
        val albedo1 = albedoAtlas.findRegion(composition.terrain1)
        val albedo2 = albedoAtlas.findRegion(composition.terrain2)
        val albedo3 = albedoAtlas.findRegion(composition.terrain3)
        val albedo4 = albedoAtlas.findRegion(composition.terrain4)

        val normal1 = normalAtlas.findRegion(composition.terrain1)
        val normal2 = normalAtlas.findRegion(composition.terrain2)
        val normal3 = normalAtlas.findRegion(composition.terrain3)
        val normal4 = normalAtlas.findRegion(composition.terrain4)

        // Set albedo UVs
        shader.setUniformf("u_terrain1_albedoUV", albedo1.u, albedo1.v, albedo1.u2, albedo1.v2)
        shader.setUniformf("u_terrain2_albedoUV", albedo2.u, albedo2.v, albedo2.u2, albedo2.v2)
        shader.setUniformf("u_terrain3_albedoUV", albedo3.u, albedo3.v, albedo3.u2, albedo3.v2)
        shader.setUniformf("u_terrain4_albedoUV", albedo4.u, albedo4.v, albedo4.u2, albedo4.v2)

        // Set normal UVs
        shader.setUniformf("u_terrain1_normalUV", normal1.u, normal1.v, normal1.u2, normal1.v2)
        shader.setUniformf("u_terrain2_normalUV", normal2.u, normal2.v, normal2.u2, normal2.v2)
        shader.setUniformf("u_terrain3_normalUV", normal3.u, normal3.v, normal3.u2, normal3.v2)
        shader.setUniformf("u_terrain4_normalUV", normal4.u, normal4.v, normal4.u2, normal4.v2)
        shader.setUniformi("u_debugMode", debugMode);
        // Set tiling
        shader.setUniformf("u_tileScale", tileScale)
    }

    fun unbind() {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
    }

    fun changeComposition(terrain1: String, terrain2: String, terrain3: String, terrain4: String) {
        composition = TerrainComposition(terrain1, terrain2, terrain3, terrain4)
    }

    private fun generateTerrainBlendMap(width: Int, height: Int): Texture {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        val noise = SimplexNoise(12345)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val nx = x / width.toFloat()
                val ny = y / height.toFloat()

                // Use smooth octave noise for each terrain type
                // Lower frequency = larger features = smoother transitions
                val noise1 = noise.octaveNoise(nx * 3f, ny * 3f, 4)
                val noise2 = noise.octaveNoise(nx * 3f + 100f, ny * 3f + 100f, 4)
                val noise3 = noise.octaveNoise(nx * 3f + 200f, ny * 3f + 200f, 4)
                val noise4 = noise.octaveNoise(nx * 3f + 300f, ny * 3f + 300f, 4)

                // Convert from [-1, 1] to [0, 1]
                val v1 = (noise1 + 1f) * 0.5f
                val v2 = (noise2 + 1f) * 0.5f
                val v3 = (noise3 + 1f) * 0.5f
                val v4 = (noise4 + 1f) * 0.5f

                // Apply a power to make transitions sharper or smoother
                val sharpness = 2f // Lower = smoother, higher = sharper
                val w1 = Math.pow(v1.toDouble(), sharpness.toDouble()).toFloat()
                val w2 = Math.pow(v2.toDouble(), sharpness.toDouble()).toFloat()
                val w3 = Math.pow(v3.toDouble(), sharpness.toDouble()).toFloat()
                val w4 = Math.pow(v4.toDouble(), sharpness.toDouble()).toFloat()

                // Normalize
                val total = w1 + w2 + w3 + w4
                val r = w1 / total
                val g = w2 / total
                val b = w3 / total
                val a = w4 / total

                pixmap.setColor(r, g, b, a)
                pixmap.drawPixel(x, y)
            }
        }

        PixmapIO.writePNG(Gdx.files.local("debug_blendmap_terrain.png"), pixmap)

        val texture = Texture(pixmap)
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        pixmap.dispose()
        return texture
    }

    private fun generateGradientBlendMap(width: Int, height: Int): Texture {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)

        val blendWidth = 0.3f // How wide the blend zone is (0.3 = 30% of image)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val nx = x / width.toFloat()
                val ny = y / height.toFloat()

                // Smooth step function for blending
                fun smoothstep(edge0: Float, edge1: Float, x: Float): Float {
                    val t = MathUtils.clamp((x - edge0) / (edge1 - edge0), 0f, 1f)
                    return t * t * (3f - 2f * t)
                }

                // Horizontal blend (left to right)
                val horizBlend = smoothstep(0.5f - blendWidth, 0.5f + blendWidth, nx)

                // Vertical blend (bottom to top)
                val vertBlend = smoothstep(0.5f - blendWidth, 0.5f + blendWidth, ny)

                // Calculate weights for 4 quadrants
                val r = (1f - horizBlend) * (1f - vertBlend)  // Bottom-left
                val g = horizBlend * (1f - vertBlend)         // Bottom-right
                val b = (1f - horizBlend) * vertBlend         // Top-left
                val a = horizBlend * vertBlend                // Top-right

                pixmap.setColor(r, g, b, a)
                pixmap.drawPixel(x, y)
            }
        }

        PixmapIO.writePNG(Gdx.files.local("debug_blendmap_gradient.png"), pixmap)

        val texture = Texture(pixmap)
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        pixmap.dispose()
        return texture
    }

    fun draw(batch: SpriteBatch, x: Float, y: Float, width: Float, height: Float) {
        batch.draw(blendMap, x, y, width, height)
    }

    fun dispose() {
        shader.dispose()
        albedoAtlas.dispose()
        normalAtlas.dispose()
        blendMap.dispose()
    }

    // Get list of all available terrain types
    fun getAvailableTerrains(): List<String> {
        return albedoAtlas.regions.map { it.name }
    }

    private fun loadShader(vertPath: String, fragPath: String): ShaderProgram {
        val vertexShader = Gdx.files.internal(vertPath).readString()
        val fragmentShader = Gdx.files.internal(fragPath).readString()

        val shader = ShaderProgram(vertexShader, fragmentShader)

        if (!shader.isCompiled) {
            Gdx.app.error("Shader", "Failed to compile shader:")
            Gdx.app.error("Shader", shader.getLog())
        }

        return shader
    }
}