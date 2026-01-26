#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture0;
uniform sampler2D u_depthTexture;
uniform vec2 u_resolution;
uniform float u_focusDistance;
uniform float u_focusRange;
uniform float u_blurStrength;
uniform vec2 u_nearFar;

float linearizeDepth(float depth) {
    float near = u_nearFar.x;
    float far = u_nearFar.y;
    float z = depth * 2.0 - 1.0;
    return (2.0 * near * far) / (far + near - z * (far - near));
}

float getBlurAmount(float depth) {
    if (depth >= 0.99) {
        return u_blurStrength;
    }

    float linearDepth = linearizeDepth(depth);
    float distanceFromFocus = linearDepth - u_focusDistance;

    if (distanceFromFocus > 0.0) {
        // Smooth gradient from 0 to blurStrength
        float blur = smoothstep(0.0, u_focusRange, distanceFromFocus);
        return blur * u_blurStrength;
    }

    return 0.0;
}

void main() {
    vec4 texColor = texture2D(u_texture0, v_texCoords);
    float depth = texture2D(u_depthTexture, v_texCoords).r;
    float blurAmount = getBlurAmount(depth);

    vec2 texelSize = 1.0 / u_resolution;

    // Key: use blurAmount directly, don't clamp to integer
    vec3 finalColor = texColor.rgb;

    if (blurAmount > 0.01) {
        vec3 blurredColor = vec3(0.0);
        float totalWeight = 0.0;

        // Sample in a grid, but weight by distance
        int maxSamples = 4;
        for (int x = -maxSamples; x <= maxSamples; x++) {
            for (int y = -maxSamples; y <= maxSamples; y++) {
                float fx = float(x);
                float fy = float(y);
                float dist = length(vec2(fx, fy));

                // Only sample within blur radius
                if (dist <= blurAmount) {
                    vec2 offset = vec2(fx, fy) * texelSize;

                    // Gaussian-like weight
                    float weight = exp(-dist * dist / (2.0 * blurAmount * blurAmount));

                    blurredColor += texture2D(u_texture0, v_texCoords + offset).rgb * weight;
                    totalWeight += weight;
                }
            }
        }

        if (totalWeight > 0.0) {
            blurredColor /= totalWeight;

            // Smooth blend based on blur amount
            float blendFactor = clamp(blurAmount / u_blurStrength, 0.0, 1.0);
            finalColor = mix(texColor.rgb, blurredColor, blendFactor);
        }
    }

    gl_FragColor = vec4(finalColor, texColor.a);
}