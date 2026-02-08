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
    float offset = u_shadowPCFOffset * 1.0;// 5x larger sampling area
    return (//getShadowness(vec2(0,0)) +
    getShadowness(vec2(offset, offset)) +
    getShadowness(vec2(-offset, offset)) +
    getShadowness(vec2(offset, -offset)) +
    getShadowness(vec2(-offset, -offset))) * 0.25;
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

#ifdef diffuseTextureFlag
varying MED vec2 v_diffuseUV;
#endif

#ifdef specularTextureFlag
varying MED vec2 v_specularUV;
#endif

#ifdef emissiveTextureFlag
varying MED vec2 v_emissiveUV;
#endif

#ifdef diffuseColorFlag
uniform vec4 u_diffuseColor;
#endif

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;
#endif

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
uniform vec3 u_baseColor;
uniform vec3 u_noiseColor;
uniform vec3 u_noiseLargeColor;
uniform float u_leafFlatColor;
uniform float u_useColorNoise;
uniform float u_noiseSmallFrequency;// NEW
uniform float u_noiseLargeFrequency;// NEW
// ==================================

// Simple 2D noise function
// Improved hash for better randomness
// Unity Simple Noise implementation - matches shader graph exactly
float unity_noise_randomValue(vec2 uv) {
    return fract(sin(dot(uv, vec2(12.9898, 78.233))) * 43758.5453);
}

float unity_noise_interpolate(float a, float b, float t) {
    return (1.0 - t) * a + (t * b);
}

float unity_valueNoise(vec2 uv) {
    vec2 i = floor(uv);
    vec2 f = fract(uv);
    f = f * f * (3.0 - 2.0 * f);

    uv = abs(fract(uv) - 0.5);
    vec2 c0 = i + vec2(0.0, 0.0);
    vec2 c1 = i + vec2(1.0, 0.0);
    vec2 c2 = i + vec2(0.0, 1.0);
    vec2 c3 = i + vec2(1.0, 1.0);
    float r0 = unity_noise_randomValue(c0);
    float r1 = unity_noise_randomValue(c1);
    float r2 = unity_noise_randomValue(c2);
    float r3 = unity_noise_randomValue(c3);

    float bottomOfGrid = unity_noise_interpolate(r0, r1, f.x);
    float topOfGrid = unity_noise_interpolate(r2, r3, f.x);
    float t = unity_noise_interpolate(bottomOfGrid, topOfGrid, f.y);
    return t;
}

float UnitySimpleNoise(vec2 UV, float Scale) {
    float t = 0.0;
    float freq = pow(2.0, float(0));
    float amp = pow(0.5, float(3 - 0));
    t += unity_valueNoise(vec2(UV.x * Scale / freq, UV.y * Scale / freq)) * amp;

    freq = pow(2.0, float(1));
    amp = pow(0.5, float(3 - 1));
    t += unity_valueNoise(vec2(UV.x * Scale / freq, UV.y * Scale / freq)) * amp;

    freq = pow(2.0, float(2));
    amp = pow(0.5, float(3 - 2));
    t += unity_valueNoise(vec2(UV.x * Scale / freq, UV.y * Scale / freq)) * amp;

    return t;
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

void main() {
    #if defined(normalFlag)
    vec3 normal = v_normal;
    #endif

    #if defined(diffuseTextureFlag)
    vec2 v_diffuseUVf = vec2(v_diffuseUV.x, 1.0 - v_diffuseUV.y);
    #endif

    #if defined(emissiveTextureFlag)
    vec2 v_emissiveUVf = vec2(v_emissiveUV.x, 1.0 - v_emissiveUV.y);
    #endif

    #if defined(specularTextureFlag)
    vec2 v_specularUVf = vec2(v_specularUV.x, 1.0 - v_specularUV.y);
    #endif

    // Apply normal mapping if available
    #ifdef normalTextureFlag
    vec3 normalMap = texture2D(u_normalTexture, v_diffuseUVf).xyz * 2.0 - 1.0;
    vec3 T = normalize(v_tangent);
    vec3 B = normalize(v_binormal);
    vec3 N = normalize(v_normal);
    mat3 TBN = mat3(T, B, N);
    normal = normalize(TBN * normalMap);
    #endif

    #if defined(diffuseTextureFlag) && defined(diffuseColorFlag) && defined(colorFlag)
    vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUVf) * u_diffuseColor * v_color;
    #elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)
    vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUVf) * u_diffuseColor;
    #elif defined(diffuseTextureFlag) && defined(colorFlag)
    vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUVf) * v_color;
    #elif defined(diffuseTextureFlag)
    vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUVf);
    #elif defined(diffuseColorFlag) && defined(colorFlag)
    vec4 diffuse = u_diffuseColor * v_color;
    #elif defined(diffuseColorFlag)
    vec4 diffuse = u_diffuseColor;
    #elif defined(colorFlag)
    vec4 diffuse = v_color;
    #else
    vec4 diffuse = vec4(1.0);
    #endif

    #ifdef alphaTestFlag
    if (diffuse.a < v_alphaTest) {
        discard;
    }
    #endif

    // ===== FOLIAGE COLOR LOGIC =====
    vec2 noiseUV_small = v_worldPos.xy;
    vec2 noiseUV_large = v_worldPos.xy;

    float colorNoiseTint = UnitySimpleNoise(noiseUV_small, u_noiseSmallFrequency);
    float colorNoiseLarge = UnitySimpleNoise(noiseUV_large, u_noiseLargeFrequency);

    vec3 smallNoiseLerp = mix(u_noiseColor, u_baseColor, colorNoiseTint);
    vec3 largeNoiseLerp = mix(smallNoiseLerp, u_noiseLargeColor, colorNoiseLarge);

    vec3 diffuseColor = diffuse.rgb;

    #ifdef colorFlag
    diffuseColor = diffuse.rgb / v_color.rgb;// Undo vertex color multiplication
    #endif


    vec3 foliageColor = diffuseColor * largeNoiseLerp;



    #ifdef normalTextureFlag
    // Sample normal map as a grayscale detail texture
    vec3 normalDetail = texture2D(u_normalTexture, v_diffuseUVf).xyz;
    float detailIntensity = (normalDetail.x + normalDetail.y + normalDetail.z) / 3.0;

    // Modulate the diffuse color to add perceived detail
    foliageColor = foliageColor * mix(0.8, 1.2, detailIntensity);
    #endif


    diffuse.rgb = foliageColor;
    // ================================

    #if defined(emissiveTextureFlag) && defined(emissiveColorFlag)
    vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUVf) * u_emissiveColor;
    #elif defined(emissiveTextureFlag)
    vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUVf);
    #elif defined(emissiveColorFlag)
    vec4 emissive = u_emissiveColor;
    #else
    vec4 emissive = vec4(0.0);
    #endif

    float shadow = 1.0;
    #ifdef shadowMapFlag
    shadow = getShadow();
    // Fade out shadows for surfaces perpendicular to light (bent leaves)
    //        #ifdef normalFlag
    //        float facingUp = clamp(normal.y, 0.0, 1.0);
    //        // Surfaces facing up get full shadows, sideways surfaces get reduced shadows
    //        shadow = mix(1.0, shadow, facingUp * facingUp);  // Quadratic falloff
    //        #endif
    #endif

    #if (!defined(lightingFlag))
    gl_FragColor.rgb = diffuse.rgb + emissive.rgb;
    #elif (!defined(specularFlag))
    #if defined(ambientFlag) && defined(separateAmbientFlag)
    #ifdef shadowMapFlag
    gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + shadow * v_lightDiffuse)) + emissive.rgb;
    #else
    gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + v_lightDiffuse)) + emissive.rgb;
    #endif
    #else
    #ifdef shadowMapFlag
    gl_FragColor.rgb = shadow * (diffuse.rgb * v_lightDiffuse) + emissive.rgb;
    #else
    gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + emissive.rgb;
    #endif
    #endif
    #else
    #if defined(specularTextureFlag) && defined(specularColorFlag)
    vec3 specular = texture2D(u_specularTexture, v_specularUVf).rgb * u_specularColor.rgb * v_lightSpecular;
    #elif defined(specularTextureFlag)
    vec3 specular = texture2D(u_specularTexture, v_specularUVf).rgb * v_lightSpecular;
    #elif defined(specularColorFlag)
    vec3 specular = u_specularColor.rgb * v_lightSpecular;
    #else
    vec3 specular = v_lightSpecular;
    #endif

    #if defined(ambientFlag) && defined(separateAmbientFlag)
    #ifdef shadowMapFlag
    gl_FragColor.rgb = (diffuse.rgb * (shadow * v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;
    #else
    gl_FragColor.rgb = (diffuse.rgb * (v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;
    #endif
    #else
    #ifdef shadowMapFlag
    gl_FragColor.rgb = shadow * ((diffuse.rgb * v_lightDiffuse) + specular) + emissive.rgb;
    #else
    gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + specular + emissive.rgb;
    #endif
    #endif
    #endif

    #ifdef fogFlag
    gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fog);
    #endif

    gl_FragColor.a = diffuse.a;
}
