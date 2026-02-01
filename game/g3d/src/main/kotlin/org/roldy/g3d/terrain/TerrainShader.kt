package org.roldy.g3d.terrain

import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute
import org.roldy.core.asset.ShaderLoader
import org.roldy.core.shader.WorldShiftingShader

class TerrainShader(
    private val terrain: Terrain,
    renderable: Renderable,
) : WorldShiftingShader(renderable, Config().apply {
    vertexShader = ShaderLoader.terrainVert
    fragmentShader = ShaderLoader.terrainFrag
}, terrain.offsetProvider) {
    private val paddedWidth = AlternatingAtlasUV.getPaddedTileWidth()

    // Pre-computed UVs
    private val materialUVs by lazy { AlternatingAtlasUV.generateAllUVs() }
    private val splatMaps = terrain.mapTerrainData.splatMaps
    val u_lightDirection by Delegate()
    val u_lightColor by Delegate()
    val u_ambientLight by Delegate()
    val u_paddedTileWidth by Delegate()
    val u_normalStrength by Delegate()
    val u_textureScale by Delegate()
    val u_textureAtlas by Delegate()

    val light = renderable.environment.run {
        val lights = get(
            DirectionalLightsAttribute::class.java,
            DirectionalLightsAttribute.Type
        ) as DirectionalLightsAttribute
        lights.lights.first()
    }
    val ambientLight = renderable.environment.get(ColorAttribute::class.java, ColorAttribute.AmbientLight)
    val u_uvs = materialUVs.mapIndexed { i, vec4 ->
        program.fetchUniformLocation("u_uv$i", false) to vec4
    }

    val texture = terrain.texture.prepare(u_textureAtlas, 10)

    val u_splats = splatMaps.mapIndexed { i, tex ->
        val loc = program.fetchUniformLocation("u_splat$i", false)
        tex.prepare(loc, i + 11)
    }


    override fun render(renderable: Renderable) {

        program.setUniformf(u_textureScale, terrain.textureScale)
        program.setUniformf(u_normalStrength, terrain.normalStrength)
        program.setUniformf(u_paddedTileWidth, paddedWidth)

        program.setUniformf(u_lightDirection, light.direction)
        program.setUniformf(u_lightColor, light.color.r, light.color.g, light.color.b)
        program.setUniformf(u_ambientLight, ambientLight.color.r, ambientLight.color.g, ambientLight.color.b)

        u_uvs.forEach { (loc, uv) ->
            program.setUniformf(loc, uv.x, uv.y, uv.z, uv.w)
        }

        texture.bind()
        u_splats.forEach(TextureBind::bind)

        super.render(renderable)
    }
}