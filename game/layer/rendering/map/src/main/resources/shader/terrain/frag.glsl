#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;

// Splatmap texture (contains terrain weights in RGBA)
uniform sampler2D u_splatmap;

// Terrain textures (up to 4 per splatmap)
uniform sampler2D u_terrain0;
uniform sampler2D u_terrain1;
uniform sampler2D u_terrain2;
uniform sampler2D u_terrain3;

// Texture tiling factor
uniform float u_tileScale;

void main() {
    // Sample the splatmap to get terrain weights
    vec4 weights = texture2D(u_splatmap, v_texCoord);

    // Calculate tiled texture coordinates for detail
    vec2 tiledCoords = v_texCoord * u_tileScale;

    // Sample all terrain textures
    vec4 color0 = texture2D(u_terrain0, tiledCoords);
    vec4 color1 = texture2D(u_terrain1, tiledCoords);
    vec4 color2 = texture2D(u_terrain2, tiledCoords);
    vec4 color3 = texture2D(u_terrain3, tiledCoords);

    // DEBUG: Visualize what weights the shader is reading from splatmap
    gl_FragColor = vec4(weights.r, weights.g, weights.b, 1.0);
    return;

    // Blend terrains based on weights (only using RGB channels)
    vec3 finalColor = color0.rgb * weights.r +
                      color1.rgb * weights.g +
                      color2.rgb * weights.b +
                      color3.rgb * weights.a;

    // Output with full opacity
    gl_FragColor = vec4(finalColor, 1.0);
}