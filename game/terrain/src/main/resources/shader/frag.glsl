#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform sampler2D u_terrain0; // Highest priority (e.g., Water)
uniform sampler2D u_terrain1; // (e.g., Grass)
uniform sampler2D u_terrain2; // (e.g., Sand)
uniform sampler2D u_terrain3; // Lowest priority (e.g., Rock)

uniform sampler2D u_splatmap; // RGBA channels for blend weights

uniform vec2 u_tileWorldPos; // World position of this tile
uniform vec2 u_splatmapSize; // Size of the splatmap texture
uniform float u_blendDistance; // Distance over which to blend (in tiles)

void main() {
    // Calculate position in splatmap based on tile world position
    vec2 splatUV = (u_tileWorldPos + v_texCoords) / u_splatmapSize;

    // Sample blend weights from splatmap
    vec4 weights = texture2D(u_splatmap, splatUV);

    // Normalize weights so they sum to 1
    float totalWeight = weights.r + weights.g + weights.b + weights.a;
    if (totalWeight > 0.0) {
        weights /= totalWeight;
    }

    // Sample all terrain textures
    vec4 color0 = texture2D(u_terrain0, v_texCoords);
    vec4 color1 = texture2D(u_terrain1, v_texCoords);
    vec4 color2 = texture2D(u_terrain2, v_texCoords);
    vec4 color3 = texture2D(u_terrain3, v_texCoords);

    // Blend based on weights
    vec4 finalColor = color0 * weights.r +
    color1 * weights.g +
    color2 * weights.b +
    color3 * weights.a;

    gl_FragColor = finalColor * v_color;
}