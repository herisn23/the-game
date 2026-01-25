#ifdef GL_ES
precision mediump float;
#endif

#ifdef diffuseTextureFlag
varying vec2 v_diffuseUV;
#endif

varying vec3 v_normal;

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

// Color uniforms (38 colors total!)
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

uniform vec3 u_ambientLight;
uniform vec3 u_dirLightColor;
uniform vec3 u_dirLightDir;

// Distance-based mask function (from Unity shader)
float colorMask(float value) {
    float dist = distance(vec3(value), vec3(0.0));
    return clamp(1.0 - ((dist - 0.1) / max(1e-5, 1e-5)), 0.0, 1.0);
}

void main() {
    vec4 baseColor = texture2D(u_texture2, v_diffuseUV);

    // Mask textures
    vec4 mask0 = texture2D(u_texture0, v_diffuseUV);
    vec4 mask1 = texture2D(u_texture1, v_diffuseUV);
    vec4 mask6 = texture2D(u_texture6, v_diffuseUV);
    vec4 mask3 = texture2D(u_texture3, v_diffuseUV);
    vec4 mask5 = texture2D(u_texture5, v_diffuseUV);
    vec4 mask4 = texture2D(u_texture4, v_diffuseUV);
    vec4 mask7 = texture2D(u_texture7, v_diffuseUV);

    // Apply colors in order (matching Unity shader)
    vec3 color = baseColor.rgb;
    float smoothness = 0.0;
    float metallic = 0.0;

    // Gems (applied first)
    float gems3Mask = colorMask(mask7.b);
    color = mix(color, baseColor.rgb * u_gems3Color, gems3Mask);
    smoothness = mix(smoothness, u_gems3Smoothness, gems3Mask);

    float gems2Mask = colorMask(mask7.g);
    color = mix(color, baseColor.rgb * u_gems2Color, gems2Mask);
    smoothness = mix(smoothness, u_gems2Smoothness, gems2Mask);

    float gems1Mask = colorMask(mask7.r);
    color = mix(color, baseColor.rgb * u_gems1Color, gems1Mask);
    smoothness = mix(smoothness, u_gems1Smoothness, gems1Mask);

    // Feathers
    float feathers3Mask = colorMask(mask4.b);
    color = mix(color, baseColor.rgb * u_feathers3Color, feathers3Mask);

    float feathers2Mask = colorMask(mask4.g);
    color = mix(color, baseColor.rgb * u_feathers2Color, feathers2Mask);

    float feathers1Mask = colorMask(mask4.r);
    color = mix(color, baseColor.rgb * u_feathers1Color, feathers1Mask);

    // Cloth
    float cloth3Mask = colorMask(mask5.b);
    color = mix(color, baseColor.rgb * u_cloth3Color, cloth3Mask);

    float cloth2Mask = colorMask(mask5.g);
    color = mix(color, baseColor.rgb * u_cloth2Color, cloth2Mask);

    float cloth1Mask = colorMask(mask5.r);
    color = mix(color, baseColor.rgb * u_cloth1Color, cloth1Mask);

    // Leather
    float leather3Mask = colorMask(mask3.b);
    color = mix(color, baseColor.rgb * u_leather3Color, leather3Mask);
    smoothness = mix(smoothness, u_leather3Smoothness, leather3Mask);

    float leather2Mask = colorMask(mask3.g);
    color = mix(color, baseColor.rgb * u_leather2Color, leather2Mask);
    smoothness = mix(smoothness, u_leather2Smoothness, leather2Mask);

    float leather1Mask = colorMask(mask3.r);
    color = mix(color, baseColor.rgb * u_leather1Color, leather1Mask);
    smoothness = mix(smoothness, u_leather1Smoothness, leather1Mask);

    // Metals
    float metal3Mask = colorMask(mask6.b);
    color = mix(color, baseColor.rgb * u_metal3Color, metal3Mask);
    metallic = mix(metallic, u_metal3Metallic, metal3Mask);
    smoothness = mix(smoothness, u_metal3Smoothness, metal3Mask);

    float metal2Mask = colorMask(mask6.g);
    color = mix(color, baseColor.rgb * u_metal2Color, metal2Mask);
    metallic = mix(metallic, u_metal2Metallic, metal2Mask);
    smoothness = mix(smoothness, u_metal2Smoothness, metal2Mask);

    float metal1Mask = colorMask(mask6.r);
    color = mix(color, baseColor.rgb * u_metal1Color, metal1Mask);
    metallic = mix(metallic, u_metal1Metallic, metal1Mask);
    smoothness = mix(smoothness, u_metal1Smoothness, metal1Mask);

    // Face details
    float scarsMask = colorMask(mask1.b);
    color = mix(color, baseColor.rgb * u_scarsColor, scarsMask);
    smoothness = mix(smoothness, u_scarsSmoothness, scarsMask);

    float lipsMask = colorMask(mask1.g);
    color = mix(color, baseColor.rgb * u_lipsColor, lipsMask);
    smoothness = mix(smoothness, u_lipsSmoothness, lipsMask);

    float scleraMask = colorMask(mask1.r);
    color = mix(color, baseColor.rgb * u_scleraColor, scleraMask);
    smoothness = mix(smoothness, u_scleraSmoothness, scleraMask);

    // Skin/Hair/Eyes
    float eyesMask = colorMask(mask0.b);
    color = mix(color, baseColor.rgb * u_eyesColor, eyesMask);
    smoothness = mix(smoothness, u_eyesSmoothness, eyesMask);

    float hairMask = colorMask(mask0.g);
    color = mix(color, baseColor.rgb * u_hairColor, hairMask);
    smoothness = mix(smoothness, u_hairSmoothness, hairMask);

    float skinMask = colorMask(mask0.r);
    color = mix(color, baseColor.rgb * u_skinColor, skinMask);
    smoothness = mix(smoothness, u_skinSmoothness, skinMask);

    // Better PBR-like lighting
    vec3 normal = normalize(v_normal);
    vec3 lightDir = normalize(-u_dirLightDir);
    vec3 viewDir = vec3(0.0, 0.0, 1.0);// Camera direction (simplified)
    vec3 halfDir = normalize(lightDir + viewDir);

    float NdotL = max(dot(normal, lightDir), 0.0);
    float NdotH = max(dot(normal, halfDir), 0.0);

    // PBR: Metals have dark diffuse, bright specular
    // Non-metals have normal diffuse, weak specular
    vec3 diffuse = color * (1.0 - metallic);// Metals don't have diffuse
    vec3 specular = mix(vec3(0.04), color, metallic);// Metals use albedo as specular color

    // Roughness is inverse of smoothness
    float roughness = 1.0 - smoothness;
    float shininess = (1.0 - roughness) * 512.0;// Convert to specular power

    // Calculate lighting
    vec3 diffuseLight = diffuse * u_ambientLight;
    diffuseLight += diffuse * u_dirLightColor * NdotL;

    // Strong specular for metals
    float specPower = pow(NdotH, shininess);
    vec3 specularLight = specular * u_dirLightColor * specPower * smoothness;

    // Boost specular for metals
    specularLight *= (1.0 + metallic * 2.0);

    vec3 finalColor = diffuseLight + specularLight;

    gl_FragColor = vec4(finalColor, 1.0);
}