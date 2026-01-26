#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_depthTexture;
uniform sampler2D u_noiseTexture;
uniform vec2 u_resolution;
uniform float u_radius;
uniform float u_bias;
uniform float u_intensity;

const int KERNEL_SIZE = 16;
uniform vec3 u_kernel[KERNEL_SIZE];

float linearizeDepth(float d, float near, float far) {
    float z = d * 2.0 - 1.0;
    return (2.0 * near * far) / (far + near - z * (far - near));
}

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    float depth = texture2D(u_depthTexture, v_texCoords).r;

    // Skip skybox
    if (depth >= 0.999) {
        gl_FragColor = color;
        return;
    }

    vec2 noiseScale = u_resolution / 4.0;
    vec3 randomVec = texture2D(u_noiseTexture, v_texCoords * noiseScale).xyz * 2.0 - 1.0;

    float occlusion = 0.0;
    float linearDepth = linearizeDepth(depth, 0.1, 1000.0);

    for (int i = 0; i < KERNEL_SIZE; i++) {
        vec3 samplePos = u_kernel[i];

        // Rotate by random vector
        vec3 tangent = normalize(randomVec - samplePos * dot(randomVec, samplePos));
        vec3 bitangent = cross(samplePos, tangent);
        mat3 TBN = mat3(tangent, bitangent, samplePos);

        vec2 sampleUV = v_texCoords + (TBN * samplePos).xy * u_radius / linearDepth;
        float sampleDepth = linearizeDepth(texture2D(u_depthTexture, sampleUV).r, 0.1, 1000.0);

        float rangeCheck = smoothstep(0.0, 1.0, u_radius / abs(linearDepth - sampleDepth));
        occlusion += (sampleDepth <= linearDepth - u_bias ? 1.0 : 0.0) * rangeCheck;
    }

    occlusion = 1.0 - (occlusion / float(KERNEL_SIZE)) * u_intensity;
    gl_FragColor = vec4(color.rgb * occlusion, color.a);
}