#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
varying vec4 v_color;

// Splatmaps
uniform sampler2D u_splatmap1;
uniform sampler2D u_splatmap2;

// Single texture atlas containing all terrain textures
uniform sampler2D u_terrainAtlas;

// Atlas regions for each terrain type (vec4 = x, y, width, height in normalized coordinates)
uniform vec4 u_atlasRegion0; // Deep water
uniform vec4 u_atlasRegion1; // Shallow water
uniform vec4 u_atlasRegion2; // Sand
uniform vec4 u_atlasRegion3; // Grass
uniform vec4 u_atlasRegion4; // Forest
uniform vec4 u_atlasRegion5; // Rock
uniform vec4 u_atlasRegion6; // Snow
uniform vec4 u_atlasRegion7; // Desert

// UV scale for tiling
uniform float u_textureScale;

// Sample from atlas region with tiling
vec4 sampleAtlasRegion(vec4 region, vec2 uv) {
    // Tile the UV coordinates
    vec2 tiledUV = fract(uv * u_textureScale);

    // Map to atlas region
    vec2 atlasUV = region.xy + tiledUV * region.zw;

    return texture2D(u_terrainAtlas, atlasUV);
}

void main() {
    // Sample splatmaps to get terrain weights
    vec4 splat1 = texture2D(u_splatmap1, v_texCoord);
    vec4 splat2 = texture2D(u_splatmap2, v_texCoord);

    // Sample all terrain textures from atlas
    vec4 color = vec4(0.0);
    color += sampleAtlasRegion(u_atlasRegion0, v_texCoord) * splat1.r;
    color += sampleAtlasRegion(u_atlasRegion1, v_texCoord) * splat1.g;
    color += sampleAtlasRegion(u_atlasRegion2, v_texCoord) * splat1.b;
    color += sampleAtlasRegion(u_atlasRegion3, v_texCoord) * splat1.a;
    color += sampleAtlasRegion(u_atlasRegion4, v_texCoord) * splat2.r;
    color += sampleAtlasRegion(u_atlasRegion5, v_texCoord) * splat2.g;
    color += sampleAtlasRegion(u_atlasRegion6, v_texCoord) * splat2.b;
    color += sampleAtlasRegion(u_atlasRegion7, v_texCoord) * splat2.a;

    gl_FragColor = color * v_color;
}