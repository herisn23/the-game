precision highp float;
#if    defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
#define ambientFlag
#endif//ambientFlag

#ifdef diffuseTextureFlag
varying vec2 v_diffuseUV;
#endif

varying vec3 v_normal;

// Lighting from vertex shader
varying vec3 v_lightDiffuse;
#ifdef specularFlag
varying vec3 v_lightSpecular;
#endif
#if defined(ambientFlag) && defined(separateAmbientFlag)
varying vec3 v_ambientLight;
#endif

// Shadow map
#ifdef shadowMapFlag
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;
#define separateAmbientFlag

float getShadowness(vec2 offset)
{
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));
}

float getShadow()
{
    return (//getShadowness(vec2(0,0)) +
    getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}
#endif

// Base texture
uniform sampler2D u_diffuseTexture;

// Base color texture (the one that gets tinted)
uniform sampler2D u_texture2;

// Mask textures
uniform sampler2D u_texture0;// Skin/Hair/Eyes
uniform sampler2D u_texture1;// Lips/Scars/Sclera
uniform sampler2D u_texture6;// Metals
uniform sampler2D u_texture3;// Leather
uniform sampler2D u_texture5;// Cloth
uniform sampler2D u_texture4;// Feathers
uniform sampler2D u_texture7;// Gems

// Color uniforms (base colors 0-1)
uniform vec3 u_skinColor;
uniform vec3 u_eyesColor;
uniform vec3 u_hairColor;
uniform vec3 u_scleraColor;
uniform vec3 u_lipsColor;
uniform vec3 u_scarsColor;
uniform vec3 u_metal1Color;
uniform vec3 u_metal2Color;
uniform vec3 u_metal3Color;
uniform vec3 u_leather1Color;
uniform vec3 u_leather2Color;
uniform vec3 u_leather3Color;
uniform vec3 u_cloth1Color;
uniform vec3 u_cloth2Color;
uniform vec3 u_cloth3Color;
uniform vec3 u_gems1Color;
uniform vec3 u_gems2Color;
uniform vec3 u_gems3Color;
uniform vec3 u_feathers1Color;
uniform vec3 u_feathers2Color;
uniform vec3 u_feathers3Color;

// HDR intensity multipliers
uniform float u_skinColorIntensity;
uniform float u_eyesColorIntensity;
uniform float u_hairColorIntensity;
uniform float u_scleraColorIntensity;
uniform float u_lipsColorIntensity;
uniform float u_scarsColorIntensity;
uniform float u_metal1ColorIntensity;
uniform float u_metal2ColorIntensity;
uniform float u_metal3ColorIntensity;
uniform float u_leather1ColorIntensity;
uniform float u_leather2ColorIntensity;
uniform float u_leather3ColorIntensity;
uniform float u_cloth1ColorIntensity;
uniform float u_cloth2ColorIntensity;
uniform float u_cloth3ColorIntensity;
uniform float u_gems1ColorIntensity;
uniform float u_gems2ColorIntensity;
uniform float u_gems3ColorIntensity;
uniform float u_feathers1ColorIntensity;
uniform float u_feathers2ColorIntensity;
uniform float u_feathers3ColorIntensity;

// Smoothness/Metallic
uniform float u_skinSmoothness;
uniform float u_eyesSmoothness;
uniform float u_hairSmoothness;
uniform float u_scleraSmoothness;
uniform float u_lipsSmoothness;
uniform float u_scarsSmoothness;
uniform float u_metal1Metallic;
uniform float u_metal1Smoothness;
uniform float u_metal2Metallic;
uniform float u_metal2Smoothness;
uniform float u_metal3Metallic;
uniform float u_metal3Smoothness;
uniform float u_leather1Smoothness;
uniform float u_leather2Smoothness;
uniform float u_leather3Smoothness;
uniform float u_gems1Smoothness;
uniform float u_gems2Smoothness;
uniform float u_gems3Smoothness;

// Distance-based mask function (from Unity shader)
float colorMask(float value) {
    float dist = distance(vec3(value), vec3(0.0));
    return clamp(1.0 - ((dist - 0.1) / max(1e-5, 1e-5)), 0.0, 1.0);
}

void main() {
    vec2 uv = vec2(v_diffuseUV.x, 1.0 - v_diffuseUV.y);
    vec4 baseColor = texture2D(u_texture2, uv);

    // Reconstruct HDR colors by multiplying base * intensity
    vec3 skinColorHDR = u_skinColor * u_skinColorIntensity;
    vec3 eyesColorHDR = u_eyesColor * u_eyesColorIntensity;
    vec3 hairColorHDR = u_hairColor * u_hairColorIntensity;
    vec3 scleraColorHDR = u_scleraColor * u_scleraColorIntensity;
    vec3 lipsColorHDR = u_lipsColor * u_lipsColorIntensity;
    vec3 scarsColorHDR = u_scarsColor * u_scarsColorIntensity;
    vec3 metal1ColorHDR = u_metal1Color * u_metal1ColorIntensity;
    vec3 metal2ColorHDR = u_metal2Color * u_metal2ColorIntensity;
    vec3 metal3ColorHDR = u_metal3Color * u_metal3ColorIntensity;
    vec3 leather1ColorHDR = u_leather1Color * u_leather1ColorIntensity;
    vec3 leather2ColorHDR = u_leather2Color * u_leather2ColorIntensity;
    vec3 leather3ColorHDR = u_leather3Color * u_leather3ColorIntensity;
    vec3 cloth1ColorHDR = u_cloth1Color * u_cloth1ColorIntensity;
    vec3 cloth2ColorHDR = u_cloth2Color * u_cloth2ColorIntensity;
    vec3 cloth3ColorHDR = u_cloth3Color * u_cloth3ColorIntensity;
    vec3 gems1ColorHDR = u_gems1Color * u_gems1ColorIntensity;
    vec3 gems2ColorHDR = u_gems2Color * u_gems2ColorIntensity;
    vec3 gems3ColorHDR = u_gems3Color * u_gems3ColorIntensity;
    vec3 feathers1ColorHDR = u_feathers1Color * u_feathers1ColorIntensity;
    vec3 feathers2ColorHDR = u_feathers2Color * u_feathers2ColorIntensity;
    vec3 feathers3ColorHDR = u_feathers3Color * u_feathers3ColorIntensity;

    // Mask textures
    vec4 mask0 = texture2D(u_texture0, uv);
    vec4 mask1 = texture2D(u_texture1, uv);
    vec4 mask6 = texture2D(u_texture6, uv);
    vec4 mask3 = texture2D(u_texture3, uv);
    vec4 mask5 = texture2D(u_texture5, uv);
    vec4 mask4 = texture2D(u_texture4, uv);
    vec4 mask7 = texture2D(u_texture7, uv);

    // Apply colors in order (matching Unity shader) - now using HDR colors
    vec3 color = baseColor.rgb;
    float smoothness = 0.0;
    float metallic = 0.0;

    // Gems (applied first)
    float gems3Mask = colorMask(mask7.b);
    color = mix(color, baseColor.rgb * gems3ColorHDR, gems3Mask);
    smoothness = mix(smoothness, u_gems3Smoothness, gems3Mask);

    float gems2Mask = colorMask(mask7.g);
    color = mix(color, baseColor.rgb * gems2ColorHDR, gems2Mask);
    smoothness = mix(smoothness, u_gems2Smoothness, gems2Mask);

    float gems1Mask = colorMask(mask7.r);
    color = mix(color, baseColor.rgb * gems1ColorHDR, gems1Mask);
    smoothness = mix(smoothness, u_gems1Smoothness, gems1Mask);

    // Feathers
    float feathers3Mask = colorMask(mask4.b);
    color = mix(color, baseColor.rgb * feathers3ColorHDR, feathers3Mask);

    float feathers2Mask = colorMask(mask4.g);
    color = mix(color, baseColor.rgb * feathers2ColorHDR, feathers2Mask);

    float feathers1Mask = colorMask(mask4.r);
    color = mix(color, baseColor.rgb * feathers1ColorHDR, feathers1Mask);

    // Cloth
    float cloth3Mask = colorMask(mask5.b);
    color = mix(color, baseColor.rgb * cloth3ColorHDR, cloth3Mask);

    float cloth2Mask = colorMask(mask5.g);
    color = mix(color, baseColor.rgb * cloth2ColorHDR, cloth2Mask);

    float cloth1Mask = colorMask(mask5.r);
    color = mix(color, baseColor.rgb * cloth1ColorHDR, cloth1Mask);

    // Leather
    float leather3Mask = colorMask(mask3.b);
    color = mix(color, baseColor.rgb * leather3ColorHDR, leather3Mask);
    smoothness = mix(smoothness, u_leather3Smoothness, leather3Mask);

    float leather2Mask = colorMask(mask3.g);
    color = mix(color, baseColor.rgb * leather2ColorHDR, leather2Mask);
    smoothness = mix(smoothness, u_leather2Smoothness, leather2Mask);

    float leather1Mask = colorMask(mask3.r);
    color = mix(color, baseColor.rgb * leather1ColorHDR, leather1Mask);
    smoothness = mix(smoothness, u_leather1Smoothness, leather1Mask);

    // Metals
    float metal3Mask = colorMask(mask6.b);
    color = mix(color, baseColor.rgb * metal3ColorHDR, metal3Mask);
    metallic = mix(metallic, u_metal3Metallic, metal3Mask);
    smoothness = mix(smoothness, u_metal3Smoothness, metal3Mask);

    float metal2Mask = colorMask(mask6.g);
    color = mix(color, baseColor.rgb * metal2ColorHDR, metal2Mask);
    metallic = mix(metallic, u_metal2Metallic, metal2Mask);
    smoothness = mix(smoothness, u_metal2Smoothness, metal2Mask);

    float metal1Mask = colorMask(mask6.r);
    color = mix(color, baseColor.rgb * metal1ColorHDR, metal1Mask);
    metallic = mix(metallic, u_metal1Metallic, metal1Mask);
    smoothness = mix(smoothness, u_metal1Smoothness, metal1Mask);

    // Face details
    float scarsMask = colorMask(mask1.b);
    color = mix(color, baseColor.rgb * scarsColorHDR, scarsMask);
    smoothness = mix(smoothness, u_scarsSmoothness, scarsMask);

    float lipsMask = colorMask(mask1.g);
    color = mix(color, baseColor.rgb * lipsColorHDR, lipsMask);
    smoothness = mix(smoothness, u_lipsSmoothness, lipsMask);

    float scleraMask = colorMask(mask1.r);
    color = mix(color, baseColor.rgb * scleraColorHDR, scleraMask);
    smoothness = mix(smoothness, u_scleraSmoothness, scleraMask);

    // Skin/Hair/Eyes
    float eyesMask = colorMask(mask0.b);
    color = mix(color, baseColor.rgb * eyesColorHDR, eyesMask);
    smoothness = mix(smoothness, u_eyesSmoothness, eyesMask);

    float hairMask = colorMask(mask0.g);
    color = mix(color, baseColor.rgb * hairColorHDR, hairMask);
    smoothness = mix(smoothness, u_hairSmoothness, hairMask);

    float skinMask = colorMask(mask0.r);
    color = mix(color, baseColor.rgb * skinColorHDR, skinMask);
    smoothness = mix(smoothness, u_skinSmoothness, skinMask);

    // PBR material response using vertex shader lighting
    vec3 diffuse = color * (1.0 - metallic);
    vec3 specular = mix(vec3(0.04), color, metallic);
    vec3 emissive = vec3(0.0, 0.0, 0.0);

    #if (!defined(lightingFlag))
    gl_FragColor.rgb = diffuse.rgb + emissive.rgb;
    #elif (!defined(specularFlag))
    #if defined(ambientFlag) && defined(separateAmbientFlag)
    #ifdef shadowMapFlag
    gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + getShadow() * v_lightDiffuse)) + emissive.rgb;
    //gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);
    #else
    gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + v_lightDiffuse)) + emissive.rgb;
    #endif//shadowMapFlag
    #else
    #ifdef shadowMapFlag
    gl_FragColor.rgb = getShadow() * (diffuse.rgb * v_lightDiffuse) + emissive.rgb;
    #else
    gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + emissive.rgb;
    #endif//shadowMapFlag
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
    gl_FragColor.rgb = (diffuse.rgb * (getShadow() * v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;
    //gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);
    #else
    gl_FragColor.rgb = (diffuse.rgb * (v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;
    #endif//shadowMapFlag
    #else
    #ifdef shadowMapFlag
    gl_FragColor.rgb = getShadow() * ((diffuse.rgb * v_lightDiffuse) + specular) + emissive.rgb;
    #else
    gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + specular + emissive.rgb;
    #endif//shadowMapFlag
    #endif
    #endif//lightingFlag


    //    // Tone mapping
    float exposure = 0.8;
    gl_FragColor.rgb *= exposure;
    gl_FragColor.rgb = gl_FragColor.rgb / (1.0 + gl_FragColor.rgb);

    //    // Linear to sRGB
    gl_FragColor.rgb = pow(gl_FragColor.rgb, vec3(1.0/2.2));

    #ifdef fogFlag
    gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fog);
    #endif// end fogFlag

    #ifdef blendedFlag
    gl_FragColor.a = diffuse.a * v_opacity;
    #ifdef alphaTestFlag
    if (gl_FragColor.a <= v_alphaTest)
    discard;
    #endif
    #else
    gl_FragColor.a = 1.0;
    #endif


}