#ifdef GL_ES
precision mediump float;
#endif

#ifdef diffuseTextureFlag
varying vec2 v_diffuseUV;
#endif

varying vec3 v_normal;

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_mask01;
uniform sampler2D u_mask02;
uniform sampler2D u_mask03;
uniform sampler2D u_mask04;
uniform sampler2D u_mask05;

uniform vec3 u_colorPrimary;
uniform vec3 u_colorSecondary;
uniform vec3 u_colorLeatherPrimary;
uniform vec3 u_colorLeatherSecondary;
uniform vec3 u_colorMetalPrimary;
uniform vec3 u_colorMetalSecondary;
uniform vec3 u_colorMetalDark;
uniform vec3 u_colorHair;
uniform vec3 u_colorSkin;
uniform vec3 u_colorStubble;
uniform vec3 u_colorScar;
uniform vec3 u_colorEyes;
uniform vec3 u_colorBodyArt;
uniform float u_bodyArtAmount;

uniform vec3 u_ambientLight;
uniform vec3 u_dirLightColor;
uniform vec3 u_dirLightDir;

void main() {
    vec4 tex = texture2D(u_diffuseTexture, v_diffuseUV);
    vec4 m01 = texture2D(u_mask01, v_diffuseUV);
    vec4 m02 = texture2D(u_mask02, v_diffuseUV);
    vec4 m03 = texture2D(u_mask03, v_diffuseUV);
    vec4 m04 = texture2D(u_mask04, v_diffuseUV);
    vec4 m05 = texture2D(u_mask05, v_diffuseUV);

    vec3 color = tex.rgb;
    color = mix(color, u_colorPrimary, step(m01.r, 0.5));
    color = mix(color, u_colorSecondary, step(m01.g, 0.5));
    color = mix(color, u_colorLeatherPrimary, step(m04.r, 0.5));
    color = mix(color, u_colorLeatherSecondary, step(m04.g, 0.5));
    color = mix(color, u_colorMetalPrimary, step(m02.r, 0.5));
    color = mix(color, u_colorMetalSecondary, step(m02.g, 0.5));
    color = mix(color, u_colorMetalDark, step(m02.b, 0.5));
    color = mix(color, u_colorHair, step(m04.b, 0.5));
    color = mix(color, u_colorSkin, step(m03.r, 0.5));
    color = mix(color, u_colorStubble, step(m03.b, 0.5));
    color = mix(color, u_colorScar, step(m03.g, 0.5));
    color = mix(u_colorEyes, color, m05.r);

    // Body art with HARD threshold - only apply where m01.b < 0.1
    if (m01.b < 0.1) {
        // This is a tattoo area
        float bodyArtStrength = u_bodyArtAmount;
        color = mix(color, u_colorBodyArt, bodyArtStrength);
    }

    float NdotL = max(dot(normalize(v_normal), normalize(-u_dirLightDir)), 0.0);
    vec3 lighting = u_ambientLight + u_dirLightColor * NdotL;

    gl_FragColor = vec4(color * lighting, 1.0);
}