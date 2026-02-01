#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_emissiveTexture;
uniform vec4 u_diffuseColor;
uniform vec4 u_emissiveColor;
uniform vec3 u_lightDir;
uniform vec3 u_lightColor;// Directional light color
uniform vec3 u_ambientLight;

varying vec2 v_texCoord;
varying vec3 v_normal;

void main() {
    vec2 uv = vec2(v_texCoord.x, 1.0 - v_texCoord.y);

    // Diffuse
    vec4 texColor = texture2D(u_diffuseTexture, uv);
    vec4 albedo = texColor * u_diffuseColor;

    // Emissive
    vec4 emissive = texture2D(u_emissiveTexture, uv) * u_emissiveColor;

    // Lighting
    float NdotL = max(dot(normalize(v_normal), normalize(u_lightDir)), 0.0);
    vec3 diffuse = albedo.rgb * u_lightColor * NdotL;
    vec3 ambient = albedo.rgb * u_ambientLight;

    // Final
    vec3 finalColor = ambient + diffuse + emissive.rgb;

    gl_FragColor = vec4(finalColor, albedo.a);
}