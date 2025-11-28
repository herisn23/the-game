#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;
uniform sampler2D u_albedoAtlas;
uniform sampler2D u_normalAtlas;
uniform sampler2D u_blendMap;

uniform vec4 u_terrain1_albedoUV;
uniform vec4 u_terrain2_albedoUV;
uniform vec4 u_terrain3_albedoUV;
uniform vec4 u_terrain4_albedoUV;

uniform vec4 u_terrain1_normalUV;
uniform vec4 u_terrain2_normalUV;
uniform vec4 u_terrain3_normalUV;
uniform vec4 u_terrain4_normalUV;

uniform float u_tileScale;
uniform int u_debugMode;

vec2 remapUV(vec2 tileUV, vec4 regionUV) {
    vec2 wrappedUV = fract(tileUV);
    vec2 size = regionUV.zw - regionUV.xy;

    // Padding to prevent edge sampling
    float padding = 0.002;

    // Clamp to avoid exact edge sampling
    vec2 clampedUV = wrappedUV * (1.0 - padding * 2.0) + padding;

    return regionUV.xy + clampedUV * size;
}

void main() {
    // Sample blend map
    vec4 blend = texture2D(u_blendMap, v_texCoord);

    // DEBUG MODE 1: Show raw blend map
    if (u_debugMode == 1) {
        // Visualize RGBA as separate colors
        gl_FragColor = vec4(blend.rgb, 1.0);
        return;
    }

    // DEBUG MODE 10: Show blend map alpha channel
    if (u_debugMode == 10) {
        gl_FragColor = vec4(blend.aaa, 1.0);
        return;
    }

    // Calculate total BEFORE normalization for debugging
    float total = blend.r + blend.g + blend.b + blend.a;

    // DEBUG MODE 11: Show total as grayscale
    if (u_debugMode == 11) {
        gl_FragColor = vec4(total, total, total, 1.0);
        return;
    }

    // Normalize blend weights
    if (total > 0.001) {
        blend /= total;
    } else {
        // If all weights are zero, default to equal blend
        blend = vec4(0.25, 0.25, 0.25, 0.25);
    }

    // DEBUG MODE 7: Show normalized weights
    if (u_debugMode == 7) {
        gl_FragColor = vec4(blend.rgb, 1.0);
        return;
    }

    // Tiled UV for detail
    vec2 tiledUV = v_texCoord * u_tileScale;

    // Sample each terrain from atlas
    vec3 albedo1 = texture2D(u_albedoAtlas, remapUV(tiledUV, u_terrain1_albedoUV)).rgb;
    vec3 albedo2 = texture2D(u_albedoAtlas, remapUV(tiledUV, u_terrain2_albedoUV)).rgb;
    vec3 albedo3 = texture2D(u_albedoAtlas, remapUV(tiledUV, u_terrain3_albedoUV)).rgb;
    vec3 albedo4 = texture2D(u_albedoAtlas, remapUV(tiledUV, u_terrain4_albedoUV)).rgb;

    // DEBUG: Show individual terrains
    if (u_debugMode == 2) {
        gl_FragColor = vec4(albedo1, 1.0);
        return;
    }
    if (u_debugMode == 3) {
        gl_FragColor = vec4(albedo2, 1.0);
        return;
    }
    if (u_debugMode == 4) {
        gl_FragColor = vec4(albedo3, 1.0);
        return;
    }
    if (u_debugMode == 5) {
        gl_FragColor = vec4(albedo4, 1.0);
        return;
    }

    // Blend the albedos
    vec3 finalAlbedo = albedo1 * blend.r + albedo2 * blend.g + albedo3 * blend.b + albedo4 * blend.a;

    // DEBUG MODE 9: Show blended result without lighting
    if (u_debugMode == 9) {
        gl_FragColor = vec4(finalAlbedo, 1.0);
        return;
    }

    // Sample normals
    vec3 normal1 = texture2D(u_normalAtlas, remapUV(tiledUV, u_terrain1_normalUV)).rgb;
    vec3 normal2 = texture2D(u_normalAtlas, remapUV(tiledUV, u_terrain2_normalUV)).rgb;
    vec3 normal3 = texture2D(u_normalAtlas, remapUV(tiledUV, u_terrain3_normalUV)).rgb;
    vec3 normal4 = texture2D(u_normalAtlas, remapUV(tiledUV, u_terrain4_normalUV)).rgb;

    // Blend normals
    vec3 finalNormal = normal1 * blend.r + normal2 * blend.g + normal3 * blend.b + normal4 * blend.a;

    // Simple lighting
    vec3 lightDir = normalize(vec3(0.5, 0.5, 1.0));
    vec3 N = normalize(finalNormal * 2.0 - 1.0);
    float NdotL = max(dot(N, lightDir), 0.0);

    vec3 diffuse = finalAlbedo * NdotL;
    vec3 ambient = finalAlbedo * 0.3;

    vec3 finalColor = diffuse + ambient;

    gl_FragColor = vec4(finalColor, 1.0) * v_color;
}