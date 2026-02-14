// ssao_frag.glsl — composited version for gdx-vfx
#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;

uniform sampler2D u_texture0;// scene color from vfx chain
uniform sampler2D u_depthTexture;
uniform sampler2D u_noiseTexture;

uniform vec3 u_samples[64];
uniform mat4 u_projection;
uniform mat4 u_invProjection;
uniform vec2 u_screenSize;
uniform float u_radius;
uniform float u_bias;
uniform float u_power;

vec3 getViewPos(vec2 uv) {
	float depth = texture2D(u_depthTexture, uv).r;
	vec4 clip = vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
	vec4 viewPos = u_invProjection * clip;
	return viewPos.xyz / viewPos.w;
}

vec3 estimateNormal(vec3 fragPos, vec2 uv) {
	vec2 texel = 1.0 / u_screenSize;
	vec3 dx = getViewPos(uv + vec2(texel.x, 0.0)) - fragPos;
	vec3 dy = getViewPos(uv + vec2(0.0, texel.y)) - fragPos;
	return normalize(cross(dx, dy));
}

void main() {
	vec3 fragPos = getViewPos(v_texCoord);
	vec3 normal  = estimateNormal(fragPos, v_texCoord);

	vec2 noiseScale = u_screenSize / 4.0;
	vec3 randomVec = texture2D(u_noiseTexture, v_texCoord * noiseScale).xyz * 2.0 - 1.0;

	// Build TBN to orient kernel along surface normal
	vec3 tangent   = normalize(randomVec - normal * dot(randomVec, normal));
	vec3 bitangent = cross(normal, tangent);
	mat3 TBN       = mat3(tangent, bitangent, normal);

	float occlusion = 0.0;

	for (int i = 0; i < 64; i++) {
		vec3 samplePos = fragPos + TBN * u_samples[i] * u_radius;

		// Project to screen UV
		vec4 offset = u_projection * vec4(samplePos, 1.0);
		offset.xy /= offset.w;
		offset.xy = offset.xy * 0.5 + 0.5;

		float sampleDepth = getViewPos(offset.xy).z;

		float rangeCheck = smoothstep(0.0, 1.0, u_radius / abs(fragPos.z - sampleDepth));
		occlusion += step(samplePos.z + u_bias, sampleDepth) * rangeCheck;
	}

	occlusion = 1.0 - (occlusion / 64.0);
	occlusion = pow(occlusion, u_power);

	// Composite: multiply scene color by AO factor
	vec4 sceneColor = texture2D(u_texture0, v_texCoord);
	gl_FragColor = vec4(sceneColor.rgb * occlusion, sceneColor.a);
}