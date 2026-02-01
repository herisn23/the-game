#ifdef GL_ES
precision mediump float;
#endif

varying vec3 v_worldPos;
varying vec3 v_normal;
varying vec2 v_tiledUV;
varying vec2 v_splatUV;
varying vec3 v_viewDir;
varying vec3 v_tangent;
varying vec3 v_bitangent;

// Shadow - same as default libGDX shader
#ifdef shadowMapFlag
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;
#define separateAmbientFlag

float getShadowness(vec2 offset) {
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));
}

float getShadow() {
    // Bounds check - return fully lit if outside shadow map
    if (v_shadowMapUv.x < 0.0 || v_shadowMapUv.x > 1.0 ||
    v_shadowMapUv.y < 0.0 || v_shadowMapUv.y > 1.0 ||
    v_shadowMapUv.z < 0.0 || v_shadowMapUv.z > 1.0) {
        return 1.0;
    }

    return (
    getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))
    ) * 0.25;
}
#endif//shadowMapFlag

uniform float u_paddedTileWidth;
uniform sampler2D u_textureAtlas;

uniform sampler2D u_splat0;
uniform sampler2D u_splat1;
uniform sampler2D u_splat2;
uniform sampler2D u_splat3;
uniform sampler2D u_splat4;
uniform sampler2D u_splat5;
uniform sampler2D u_splat6;
uniform sampler2D u_splat7;

uniform vec4 u_uv0, u_uv1, u_uv2, u_uv3;
uniform vec4 u_uv4, u_uv5, u_uv6, u_uv7;
uniform vec4 u_uv8, u_uv9, u_uv10, u_uv11;
uniform vec4 u_uv12, u_uv13, u_uv14, u_uv15;
uniform vec4 u_uv16, u_uv17, u_uv18, u_uv19;
uniform vec4 u_uv20, u_uv21, u_uv22, u_uv23;
uniform vec4 u_uv24, u_uv25, u_uv26, u_uv27;
uniform vec4 u_uv28, u_uv29, u_uv30, u_uv31;

uniform vec3 u_lightDirection;
uniform vec3 u_lightColor;
uniform vec3 u_ambientLight;
uniform float u_textureScale;
uniform float u_normalStrength;

void sampleTexture(vec4 uvData, vec2 tiledUV, out vec3 albedo, out vec3 normalTS) {
    vec2 scaledUV = tiledUV * u_textureScale;
    vec2 localUV = fract(scaledUV) * uvData.zw;

    vec2 albedoUV = uvData.xy + localUV;
    albedo = texture2D(u_textureAtlas, albedoUV).rgb;

    vec2 normalUV = uvData.xy + vec2(u_paddedTileWidth, 0.0) + localUV;
    normalTS = texture2D(u_textureAtlas, normalUV).rgb * 2.0 - 1.0;
}

void main() {
    vec4 s0 = texture2D(u_splat0, v_splatUV);
    vec4 s1 = texture2D(u_splat1, v_splatUV);
    vec4 s2 = texture2D(u_splat2, v_splatUV);
    vec4 s3 = texture2D(u_splat3, v_splatUV);
    vec4 s4 = texture2D(u_splat4, v_splatUV);
    vec4 s5 = texture2D(u_splat5, v_splatUV);
    vec4 s6 = texture2D(u_splat6, v_splatUV);
    vec4 s7 = texture2D(u_splat7, v_splatUV);

    vec3 albedo = vec3(0.0);
    vec3 normalTS = vec3(0.0);
    float totalWeight = 0.0;

    vec3 tempAlbedo, tempNormal;

    // Splatmap 0
    if (s0.r > 0.001) {
        sampleTexture(u_uv0, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s0.r;
        normalTS += tempNormal * s0.r;
        totalWeight += s0.r;
    }
    if (s0.g > 0.001) {
        sampleTexture(u_uv1, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s0.g;
        normalTS += tempNormal * s0.g;
        totalWeight += s0.g;
    }
    if (s0.b > 0.001) {
        sampleTexture(u_uv2, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s0.b;
        normalTS += tempNormal * s0.b;
        totalWeight += s0.b;
    }
    if (s0.a > 0.001) {
        sampleTexture(u_uv3, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s0.a;
        normalTS += tempNormal * s0.a;
        totalWeight += s0.a;
    }

    // Splatmap 1
    if (s1.r > 0.001) {
        sampleTexture(u_uv4, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s1.r;
        normalTS += tempNormal * s1.r;
        totalWeight += s1.r;
    }
    if (s1.g > 0.001) {
        sampleTexture(u_uv5, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s1.g;
        normalTS += tempNormal * s1.g;
        totalWeight += s1.g;
    }
    if (s1.b > 0.001) {
        sampleTexture(u_uv6, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s1.b;
        normalTS += tempNormal * s1.b;
        totalWeight += s1.b;
    }
    if (s1.a > 0.001) {
        sampleTexture(u_uv7, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s1.a;
        normalTS += tempNormal * s1.a;
        totalWeight += s1.a;
    }

    // Splatmap 2
    if (s2.r > 0.001) {
        sampleTexture(u_uv8, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s2.r;
        normalTS += tempNormal * s2.r;
        totalWeight += s2.r;
    }
    if (s2.g > 0.001) {
        sampleTexture(u_uv9, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s2.g;
        normalTS += tempNormal * s2.g;
        totalWeight += s2.g;
    }
    if (s2.b > 0.001) {
        sampleTexture(u_uv10, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s2.b;
        normalTS += tempNormal * s2.b;
        totalWeight += s2.b;
    }
    if (s2.a > 0.001) {
        sampleTexture(u_uv11, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s2.a;
        normalTS += tempNormal * s2.a;
        totalWeight += s2.a;
    }

    // Splatmap 3
    if (s3.r > 0.001) {
        sampleTexture(u_uv12, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s3.r;
        normalTS += tempNormal * s3.r;
        totalWeight += s3.r;
    }
    if (s3.g > 0.001) {
        sampleTexture(u_uv13, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s3.g;
        normalTS += tempNormal * s3.g;
        totalWeight += s3.g;
    }
    if (s3.b > 0.001) {
        sampleTexture(u_uv14, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s3.b;
        normalTS += tempNormal * s3.b;
        totalWeight += s3.b;
    }
    if (s3.a > 0.001) {
        sampleTexture(u_uv15, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s3.a;
        normalTS += tempNormal * s3.a;
        totalWeight += s3.a;
    }

    // Splatmap 4
    if (s4.r > 0.001) {
        sampleTexture(u_uv16, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s4.r;
        normalTS += tempNormal * s4.r;
        totalWeight += s4.r;
    }
    if (s4.g > 0.001) {
        sampleTexture(u_uv17, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s4.g;
        normalTS += tempNormal * s4.g;
        totalWeight += s4.g;
    }
    if (s4.b > 0.001) {
        sampleTexture(u_uv18, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s4.b;
        normalTS += tempNormal * s4.b;
        totalWeight += s4.b;
    }
    if (s4.a > 0.001) {
        sampleTexture(u_uv19, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s4.a;
        normalTS += tempNormal * s4.a;
        totalWeight += s4.a;
    }

    // Splatmap 5
    if (s5.r > 0.001) {
        sampleTexture(u_uv20, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s5.r;
        normalTS += tempNormal * s5.r;
        totalWeight += s5.r;
    }
    if (s5.g > 0.001) {
        sampleTexture(u_uv21, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s5.g;
        normalTS += tempNormal * s5.g;
        totalWeight += s5.g;
    }
    if (s5.b > 0.001) {
        sampleTexture(u_uv22, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s5.b;
        normalTS += tempNormal * s5.b;
        totalWeight += s5.b;
    }
    if (s5.a > 0.001) {
        sampleTexture(u_uv23, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s5.a;
        normalTS += tempNormal * s5.a;
        totalWeight += s5.a;
    }

    // Splatmap 6
    if (s6.r > 0.001) {
        sampleTexture(u_uv24, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s6.r;
        normalTS += tempNormal * s6.r;
        totalWeight += s6.r;
    }
    if (s6.g > 0.001) {
        sampleTexture(u_uv25, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s6.g;
        normalTS += tempNormal * s6.g;
        totalWeight += s6.g;
    }
    if (s6.b > 0.001) {
        sampleTexture(u_uv26, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s6.b;
        normalTS += tempNormal * s6.b;
        totalWeight += s6.b;
    }
    if (s6.a > 0.001) {
        sampleTexture(u_uv27, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s6.a;
        normalTS += tempNormal * s6.a;
        totalWeight += s6.a;
    }

    // Splatmap 7
    if (s7.r > 0.001) {
        sampleTexture(u_uv28, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s7.r;
        normalTS += tempNormal * s7.r;
        totalWeight += s7.r;
    }
    if (s7.g > 0.001) {
        sampleTexture(u_uv29, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s7.g;
        normalTS += tempNormal * s7.g;
        totalWeight += s7.g;
    }
    if (s7.b > 0.001) {
        sampleTexture(u_uv30, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s7.b;
        normalTS += tempNormal * s7.b;
        totalWeight += s7.b;
    }
    if (s7.a > 0.001) {
        sampleTexture(u_uv31, v_tiledUV, tempAlbedo, tempNormal);
        albedo += tempAlbedo * s7.a;
        normalTS += tempNormal * s7.a;
        totalWeight += s7.a;
    }

    // Normalize
    if (totalWeight > 0.0) {
        albedo /= totalWeight;
        normalTS /= totalWeight;
    } else {
        albedo = vec3(1.0, 0.0, 1.0);
        normalTS = vec3(0.0, 0.0, 1.0);
    }

    // Apply normal strength
    normalTS.xy *= u_normalStrength;
    normalTS = normalize(normalTS);

    // TBN transform
    vec3 N = normalize(v_normal);
    vec3 T = normalize(v_tangent);
    vec3 B = normalize(v_bitangent);
    mat3 TBN = mat3(T, B, N);
    vec3 normal = normalize(TBN * normalTS);

    // Get shadow - same as working shader
    #ifdef shadowMapFlag
    float shadow = getShadow();
    #else
    float shadow = 1.0;
    #endif

    // Lighting
    vec3 lightDir = normalize(-u_lightDirection);
    float NdotL = max(dot(normal, lightDir), 0.0);

    // Ambient NOT shadowed, direct light IS shadowed
    vec3 finalColor = albedo * u_ambientLight + albedo * u_lightColor * NdotL * shadow;

    gl_FragColor = vec4(finalColor, 1.0);
}