#ifdef GL_ES
precision mediump float;
#endif

#ifdef GL_ES
precision mediump float;
#endif

varying vec3 v_worldPos;
varying vec3 v_normal;
varying vec2 v_tiledUV;
varying vec2 v_splatUV;
varying vec3 v_viewDir;

uniform sampler2D u_albedoAtlas;
uniform sampler2D u_splat0;
uniform sampler2D u_splat1;
uniform sampler2D u_splat2;
uniform sampler2D u_splat3;
uniform sampler2D u_splat4;
uniform sampler2D u_splat5;
uniform sampler2D u_splat6;

uniform vec4 u_uv0, u_uv1, u_uv2, u_uv3;
uniform vec4 u_uv4, u_uv5, u_uv6, u_uv7;
uniform vec4 u_uv8, u_uv9, u_uv10, u_uv11;
uniform vec4 u_uv12, u_uv13, u_uv14, u_uv15;
uniform vec4 u_uv16, u_uv17, u_uv18, u_uv19;
uniform vec4 u_uv20, u_uv21, u_uv22, u_uv23;
uniform vec4 u_uv24, u_uv25, u_uv26, u_uv27;

uniform vec3 u_lightDirection;
uniform vec3 u_lightColor;
uniform vec3 u_ambientLight;

uniform float u_textureScale;// Add this - higher = smaller textures (more tiling)

vec3 sampleTex(vec4 uvData, vec2 tiledUV) {
    // Apply texture scale to tiled UV
    vec2 scaledUV = tiledUV * u_textureScale;
    vec2 uv = uvData.xy + fract(scaledUV) * uvData.zw;
    return texture2D(u_albedoAtlas, uv).rgb;
}
void main() {
    // Sample all splatmaps
    vec4 s0 = texture2D(u_splat0, v_splatUV);
    vec4 s1 = texture2D(u_splat1, v_splatUV);
    vec4 s2 = texture2D(u_splat2, v_splatUV);
    vec4 s3 = texture2D(u_splat3, v_splatUV);
    vec4 s4 = texture2D(u_splat4, v_splatUV);
    vec4 s5 = texture2D(u_splat5, v_splatUV);
    vec4 s6 = texture2D(u_splat6, v_splatUV);

    vec3 albedo = vec3(0.0);
    float totalWeight = 0.0;

    // Splatmap 0: textures 0-3
    if (s0.r > 0.001) { albedo += sampleTex(u_uv0, v_tiledUV) * s0.r; totalWeight += s0.r; }
    if (s0.g > 0.001) { albedo += sampleTex(u_uv1, v_tiledUV) * s0.g; totalWeight += s0.g; }
    if (s0.b > 0.001) { albedo += sampleTex(u_uv2, v_tiledUV) * s0.b; totalWeight += s0.b; }
    if (s0.a > 0.001) { albedo += sampleTex(u_uv3, v_tiledUV) * s0.a; totalWeight += s0.a; }

    // Splatmap 1: textures 4-7
    if (s1.r > 0.001) { albedo += sampleTex(u_uv4, v_tiledUV) * s1.r; totalWeight += s1.r; }
    if (s1.g > 0.001) { albedo += sampleTex(u_uv5, v_tiledUV) * s1.g; totalWeight += s1.g; }
    if (s1.b > 0.001) { albedo += sampleTex(u_uv6, v_tiledUV) * s1.b; totalWeight += s1.b; }
    if (s1.a > 0.001) { albedo += sampleTex(u_uv7, v_tiledUV) * s1.a; totalWeight += s1.a; }

    // Splatmap 2: textures 8-11
    if (s2.r > 0.001) { albedo += sampleTex(u_uv8, v_tiledUV) * s2.r; totalWeight += s2.r; }
    if (s2.g > 0.001) { albedo += sampleTex(u_uv9, v_tiledUV) * s2.g; totalWeight += s2.g; }
    if (s2.b > 0.001) { albedo += sampleTex(u_uv10, v_tiledUV) * s2.b; totalWeight += s2.b; }
    if (s2.a > 0.001) { albedo += sampleTex(u_uv11, v_tiledUV) * s2.a; totalWeight += s2.a; }

    // Splatmap 3: textures 12-15
    if (s3.r > 0.001) { albedo += sampleTex(u_uv12, v_tiledUV) * s3.r; totalWeight += s3.r; }
    if (s3.g > 0.001) { albedo += sampleTex(u_uv13, v_tiledUV) * s3.g; totalWeight += s3.g; }
    if (s3.b > 0.001) { albedo += sampleTex(u_uv14, v_tiledUV) * s3.b; totalWeight += s3.b; }
    if (s3.a > 0.001) { albedo += sampleTex(u_uv15, v_tiledUV) * s3.a; totalWeight += s3.a; }

    // Splatmap 4: textures 16-19
    if (s4.r > 0.001) { albedo += sampleTex(u_uv16, v_tiledUV) * s4.r; totalWeight += s4.r; }
    if (s4.g > 0.001) { albedo += sampleTex(u_uv17, v_tiledUV) * s4.g; totalWeight += s4.g; }
    if (s4.b > 0.001) { albedo += sampleTex(u_uv18, v_tiledUV) * s4.b; totalWeight += s4.b; }
    if (s4.a > 0.001) { albedo += sampleTex(u_uv19, v_tiledUV) * s4.a; totalWeight += s4.a; }

    // Splatmap 5: textures 20-23
    if (s5.r > 0.001) { albedo += sampleTex(u_uv20, v_tiledUV) * s5.r; totalWeight += s5.r; }
    if (s5.g > 0.001) { albedo += sampleTex(u_uv21, v_tiledUV) * s5.g; totalWeight += s5.g; }
    if (s5.b > 0.001) { albedo += sampleTex(u_uv22, v_tiledUV) * s5.b; totalWeight += s5.b; }
    if (s5.a > 0.001) { albedo += sampleTex(u_uv23, v_tiledUV) * s5.a; totalWeight += s5.a; }

    // Splatmap 6: textures 24-27
    if (s6.r > 0.001) { albedo += sampleTex(u_uv24, v_tiledUV) * s6.r; totalWeight += s6.r; }
    if (s6.g > 0.001) { albedo += sampleTex(u_uv25, v_tiledUV) * s6.g; totalWeight += s6.g; }
    if (s6.b > 0.001) { albedo += sampleTex(u_uv26, v_tiledUV) * s6.b; totalWeight += s6.b; }
    if (s6.a > 0.001) { albedo += sampleTex(u_uv27, v_tiledUV) * s6.a; totalWeight += s6.a; }

    // Normalize
    if (totalWeight > 0.0) {
        albedo /= totalWeight;
    } else {
        // Fallback: magenta = no weights
        albedo = vec3(1.0, 0.0, 1.0);
    }

    // Lighting
    vec3 normal = normalize(v_normal);
    vec3 lightDir = normalize(-u_lightDirection);
    float NdotL = max(dot(normal, lightDir), 0.0);

    vec3 finalColor = albedo * u_ambientLight + albedo * u_lightColor * NdotL;

    gl_FragColor = vec4(finalColor, 1.0);
}