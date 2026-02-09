#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

// Shadow - same as default libGDX shader
#ifdef shadowMapFlag
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;
#define separateAmbientFlag
float getShadowness(vec2 offset)
{
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts)+(1.0/255.0));
}

float getShadow()
{
    return (//getShadowness(vec2(0,0)) +
    getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}
#endif//shadowMapFlag


#if defined(specularTextureFlag) || defined(specularColorFlag)
#define specularFlag
#endif

#ifdef normalFlag
varying vec3 v_normal;
#endif

#if defined(colorFlag)
varying vec4 v_color;
#endif

#ifdef blendedFlag
varying float v_opacity;
#ifdef alphaTestFlag
varying float v_alphaTest;
#endif
#endif

varying MED vec2 v_UV;

#ifdef specularTextureFlag
varying MED vec2 v_specularUV;
#endif

#ifdef emissiveTextureFlag
varying MED vec2 v_emissiveUV;
#endif

uniform sampler2D u_leafTexture;
uniform sampler2D u_trunkTexture;// NEW: Trunk texture

#ifdef specularColorFlag
uniform vec4 u_specularColor;
#endif

#ifdef specularTextureFlag
uniform sampler2D u_specularTexture;
#endif

#ifdef normalTextureFlag
uniform sampler2D u_normalTexture;
varying vec3 v_binormal;
varying vec3 v_tangent;
#endif

#ifdef emissiveColorFlag
uniform vec4 u_emissiveColor;
#endif

#ifdef emissiveTextureFlag
uniform sampler2D u_emissiveTexture;
#endif

// ===== FOLIAGE COLOR UNIFORMS =====
varying vec3 v_worldPos;

// Leaf colors
uniform vec3 u_leafBaseColor;
uniform vec3 u_leafNoiseColor;
uniform vec3 u_leafNoiseLargeColor;
uniform float u_leafFlatColor;

// Trunk colors
uniform vec3 u_trunkBaseColor;
uniform vec3 u_trunkNoiseColor;
uniform float u_trunkFlatColor;// 0 = use noise, 1 = flat base color only

// Noise configuration
uniform float u_useColorNoise;
uniform float u_noiseSmallFrequency;// NEW
uniform float u_noiseLargeFrequency;// NEW
// ==================================
// Hash function for pseudo-random values
vec2 hash22(vec2 p) {
    p = vec2(dot(p, vec2(127.1, 311.7)),
    dot(p, vec2(269.5, 183.3)));
    return -1.0 + 2.0 * fract(sin(p) * 43758.5453123);
}

// Simple 2D noise function (similar to Unity's Simple Noise)
float simpleNoise(vec2 uv, float scale) {
    uv *= scale;

    vec2 i = floor(uv);
    vec2 f = fract(uv);

    // Smooth interpolation (quintic)
    vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);

    // Get random gradients at corners
    vec2 ga = hash22(i + vec2(0.0, 0.0));
    vec2 gb = hash22(i + vec2(1.0, 0.0));
    vec2 gc = hash22(i + vec2(0.0, 1.0));
    vec2 gd = hash22(i + vec2(1.0, 1.0));

    // Calculate dot products
    float va = dot(ga, f - vec2(0.0, 0.0));
    float vb = dot(gb, f - vec2(1.0, 0.0));
    float vc = dot(gc, f - vec2(0.0, 1.0));
    float vd = dot(gd, f - vec2(1.0, 1.0));

    // Bilinear interpolation
    return mix(mix(va, vb, u.x),
    mix(vc, vd, u.x), u.y) * 0.5 + 0.5;
}

// Main function matching your shader graph
float colorNoise(vec3 worldPosition, float scale) {
    // Multiply position by 0.5
    vec3 scaledPos = worldPosition * 0.5;

    // Split and use R(X) and B(Z) channels as Vector2
    vec2 uv = vec2(scaledPos.x, scaledPos.y);

    // Apply simple noise with scale of 1.0
    return simpleNoise(uv, scale);
}

#ifdef lightingFlag
varying vec3 v_lightDiffuse;

#if defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
#define ambientFlag
#endif

#ifdef specularFlag
varying vec3 v_lightSpecular;
#endif

#if defined(ambientFlag) && defined(separateAmbientFlag)
varying vec3 v_ambientLight;
#endif

#endif

#ifdef fogFlag
uniform vec4 u_fogColor;
varying float v_fog;
#endif

// Add this varying at the top:
varying vec3 v_lightDirection;

bool useNoiseColor() {
    return u_useColorNoise > 0.5;
}

bool useLeafFlatColor() {
    return u_leafFlatColor > 0.5;
}

bool useTrunkFlatColor() {
    return u_trunkFlatColor > 0.5;
}

// Multiply blend for vec3 with opacity
vec3 blendMultiply(vec3 base, vec3 blend, float opacity) {
    return base * blend;
    vec3 result = base * blend;
    return mix(base, result, opacity);
}
bool isLeaf() {
    return v_color.b > 0.5;
}
vec4 getTexture() {
    if (isLeaf()) {
        return texture2D(u_leafTexture, v_UV);
    } else {
        return texture2D(u_trunkTexture, v_UV);
    }
}
void main() {
    #if defined(normalFlag)
    vec3 normal = v_normal;
    #endif

    // Apply normal mapping if available
    #ifdef normalTextureFlag
    vec3 normalMap = texture2D(u_normalTexture, v_UV).xyz * 2.0 - 1.0;
    vec3 T = normalize(v_tangent);
    vec3 B = normalize(v_binormal);
    vec3 N = normalize(v_normal);
    mat3 TBN = mat3(T, B, N);
    normal = normalize(TBN * normalMap);
    #endif

    vec4 diffuse = getTexture();

    #ifdef alphaTestFlag
    if (diffuse.a < v_alphaTest) {
        discard;
    }
    #endif

    bool useNoiseColor = useNoiseColor();

    vec3 blended = diffuse.rgb;
    //    vec3 color;
    //
    //    if(isLeaf()) {
    //        color = u_leafBaseColor;
    //    } else {
    //        color = u_trunkBaseColor;
    //    }
    //
    //    if(useNoiseColor) {
    //        float smallNoise = colorNoise(v_worldPos, u_noiseSmallFrequency);
    //        if(!isLeaf()) {
    //            color = mix(u_trunkNoiseColor, u_trunkBaseColor, smallNoise);
    //        } else {
    //            float largeNoise = colorNoise(v_worldPos, u_noiseLargeFrequency);
    //            vec3 smallNoiseResult = mix(u_leafNoiseColor, u_leafBaseColor, smallNoise);
    //            color = mix(smallNoiseResult, u_leafNoiseLargeColor, largeNoise);
    //        }
    ////        color *= 0.7;
    //
    //        // 0.7 darker result (do not know why but direct largeNoiseResult is too bright)
    //        blended = blendMultiply(diffuse.rgb, color, 1.0);
    //    }
    vec4 baseColor = diffuse;
    //
    //    if(isLeaf() && useLeafFlatColor()) {
    //        baseColor = vec4(color, diffuse.a);
    //    }
    //    if(!isLeaf() && useTrunkFlatColor()) {
    //        baseColor = vec4(color, diffuse.a);
    //    }



    #ifdef normalTextureFlag
    // Sample normal map as a grayscale detail texture
    vec3 normalDetail = texture2D(u_normalTexture, v_UV).xyz;
    float detailIntensity = (normalDetail.x + normalDetail.y + normalDetail.z) / 3.0;

    // Modulate the diffuse color to add perceived detail
    baseColor.rgb = baseColor.rgb * mix(0.8, 1.2, detailIntensity);
    #endif

    // ================================

    #if defined(emissiveTextureFlag) && defined(emissiveColorFlag)
    vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUV) * u_emissiveColor;
    #elif defined(emissiveTextureFlag)
    vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUV);
    #elif defined(emissiveColorFlag)
    vec4 emissive = u_emissiveColor;
    #else
    vec4 emissive = vec4(0.0);
    #endif

    float shadow = 1.0;
    #ifdef shadowMapFlag
    shadow = getShadow();
    #endif


    #if (!defined(lightingFlag))
    gl_FragColor = baseColor.rgb + emissive.rgb;
    #elif (!defined(specularFlag))
    #if defined(ambientFlag) && defined(separateAmbientFlag)
    #ifdef shadowMapFlag
    gl_FragColor.rgb = (baseColor.rgb * (v_ambientLight + shadow * v_lightDiffuse)) + emissive.rgb;
    #else
    gl_FragColor.rgb = (baseColor.rgb * (v_ambientLight + v_lightDiffuse)) + emissive.rgb;
    #endif
    #else
    #ifdef shadowMapFlag
    gl_FragColor.rgb = shadow * (baseColor.rgb * v_lightDiffuse) + emissive.rgb;
    #else
    gl_FragColor.rgb = (baseColor.rgb * v_lightDiffuse) + emissive.rgb;
    #endif
    #endif
    #else
    #if defined(specularTextureFlag) && defined(specularColorFlag)
    vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * u_specularColor.rgb * v_lightSpecular;
    #elif defined(specularTextureFlag)
    vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * v_lightSpecular;
    #elif defined(specularColorFlag)
    vec3 specular = u_specularColor.rgb * v_lightSpecular;
    #else
    vec3 specular = v_lightSpecular;
    #endif

    #if defined(ambientFlag) && defined(separateAmbientFlag)
    #ifdef shadowMapFlag
    gl_FragColor.rgb = (baseColor.rgb * (shadow * v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;
    #else
    gl_FragColor.rgb = (baseColor.rgb * (v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;
    #endif
    #else
    #ifdef shadowMapFlag
    gl_FragColor.rgb = shadow * ((baseColor.rgb * v_lightDiffuse) + specular) + emissive.rgb;
    #else
    gl_FragColor.rgb = (baseColor.rgb * v_lightDiffuse) + specular + emissive.rgb;
    #endif
    #endif
    #endif

    #ifdef fogFlag
    gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fog);
    #endif


    gl_FragColor.a = baseColor.a;


}
