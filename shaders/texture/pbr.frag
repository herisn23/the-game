#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;
uniform sampler2D u_ao;
uniform sampler2D u_height;
uniform sampler2D u_mask;
uniform sampler2D u_metallic;
uniform sampler2D u_normal;
uniform sampler2D u_roughness;

void main() {
    // Sample all textures
    vec3 albedo = texture2D(u_texture, v_texCoord).rgb;
    float ao = texture2D(u_ao, v_texCoord).r;
    float height = texture2D(u_height, v_texCoord).r;
    float mask = texture2D(u_mask, v_texCoord).r;
    float metallic = texture2D(u_metallic, v_texCoord).r;
    vec3 normal = texture2D(u_normal, v_texCoord).rgb;
    float roughness = texture2D(u_roughness, v_texCoord).r;

    // Simple lighting calculation
    vec3 lightDir = normalize(vec3(0.5, 0.5, 1.0));

    // Convert normal from [0,1] to [-1,1] range
    vec3 N = normalize(normal * 2.0 - 1.0);

    // Basic diffuse lighting
    float NdotL = max(dot(N, lightDir), 0.0);

    // Combine everything
    vec3 diffuse = albedo * NdotL;
    vec3 ambient = albedo * 0.3;

    // Apply AO to ambient
    ambient *= ao;

    vec3 finalColor = diffuse + ambient;

    // Apply mask as alpha and vertex color
    gl_FragColor = vec4(finalColor, mask) * v_color;
}