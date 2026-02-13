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
	return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts)+(1.0/255.0));//
}

float getShadow()
{
	float shadow = (//getShadowness(vec2(0,0)) +
	getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
	getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
	getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
	getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;

	return mix(0.5, 1.0, shadow);
}
#endif//shadowMapFlag


varying vec3 v_normal;
varying vec4 v_color;
varying float v_alphaTest;

varying MED vec2 v_UV;

uniform sampler2D u_leafTexture;
uniform sampler2D u_trunkTexture;// NEW: Trunk texture

varying vec3 v_binormal;
varying vec3 v_tangent;

uniform vec4 u_cameraPosition;

// ===== FOLIAGE COLOR UNIFORMS =====
varying vec3 v_worldPos;

uniform sampler2D u_trunkNormal;
uniform sampler2D u_leafNormal;

uniform float u_trunkMetallic;
uniform float u_leafMetallic;
uniform float u_trunkSmoothness;
uniform float u_leafSmoothness;

uniform float u_leafNormalStrength;
uniform float u_trunkNormalStrength;

uniform bool u_leafHasNormal;
uniform bool u_trunkHasNormal;

// Leaf colors
uniform vec3 u_leafBaseColor;
uniform vec3 u_leafNoiseColor;
uniform vec3 u_leafNoiseLargeColor;
uniform bool u_leafFlatColor;

// Trunk colors
uniform vec3 u_trunkBaseColor;
uniform vec3 u_trunkNoiseColor;
uniform bool u_trunkFlatColor;// 0 = use noise, 1 = flat base color only

// Noise configuration
uniform bool u_useColorNoise;
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
varying vec3 v_lightDiffuse;
varying vec3 v_ambientLight;

#ifdef fogFlag
uniform vec4 u_fogColor;
varying float v_fog;
#endif

// Add this varying at the top:
varying vec3 v_lightDirection;


// Multiply blend for vec3 with opacity
vec3 blendMultiply(vec3 base, vec3 blend, float opacity) {
	//        return base * blend;
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

float getNormal(sampler2D texture, float strength) {
	vec3 normalDetail = texture2D(texture, v_UV).xyz;
	float detailIntensity = (normalDetail.x + normalDetail.y + normalDetail.z) / 3.0;

	// Apply strength - blend between neutral (0.5) and the actual intensity
	detailIntensity = mix(0.5, detailIntensity, strength);

	return mix(0.8, 1.2, detailIntensity);
}

void main() {
	//debug code to check if trunk(black) or leaf(blue)
	//    gl_FragColor.a = 1.0;
	//    gl_FragColor.r = 0.0;
	//    gl_FragColor.g = 0.0;
	//    gl_FragColor.b = v_color.b;
	//    return;

	vec4 diffuse = getTexture();

	#ifdef alphaTestFlag
	if (diffuse.a < v_alphaTest) {
		discard;
	}
	#endif

	bool isLeaf = isLeaf();


	vec3 targetColor;

	if (isLeaf) {
		vec3 color1 = u_leafBaseColor;
		vec3 color2 = diffuse.rgb;

		if (u_useColorNoise) {
			float smallNoise = colorNoise(v_worldPos, u_noiseSmallFrequency);
			float largeNoise = colorNoise(v_worldPos, u_noiseLargeFrequency);
			vec3 smallNoiseResult = mix(u_leafNoiseColor, u_leafBaseColor, smallNoise);
			vec3 largeNoiseResult = mix(smallNoiseResult, u_leafNoiseLargeColor, largeNoise);
			color1 = largeNoiseResult;
			color2 = blendMultiply(diffuse.rgb, color1, 1.0);
		}
		targetColor = color2;
		if (u_leafFlatColor) {
			targetColor = color1;
		}
		//        targetColor = vec3(0.0, 1.0, 0.0);
	} else {
		float smallNoise = colorNoise(v_worldPos, u_noiseSmallFrequency);
		vec3 color1 = mix(u_trunkNoiseColor, u_trunkBaseColor, smallNoise);
		vec3 color2 = diffuse.rgb;

		if (u_useColorNoise) {
			color2 = blendMultiply(diffuse.rgb, color1, 1.0);
		}
		targetColor = color2;
		if (u_trunkFlatColor) {
			targetColor = color1;
		}
		//        targetColor = vec3(1.0, 0.0, 0.0);
	}

	vec4 baseColor = vec4(targetColor, diffuse.a);

	// Apply normal mapping if available
	if (isLeaf && u_leafHasNormal) {
		baseColor.rgb = baseColor.rgb * getNormal(u_leafNormal, u_leafNormalStrength);
	} else if (u_trunkHasNormal) {
		baseColor.rgb = baseColor.rgb * getNormal(u_trunkNormal, u_trunkNormalStrength);
	}


	float metallic;
	float smoothness;

	if (isLeaf) {
		metallic = u_leafMetallic;
		smoothness = u_leafSmoothness;
	} else {
		metallic = u_trunkMetallic;
		smoothness = u_trunkSmoothness;
	}

	// ================================

	vec4 emissive = vec4(0.0);

	float shadow = 1.0;
	#ifdef shadowMapFlag
	shadow = getShadow();
	#endif

	vec3 normal = normalize(v_normal);
	vec3 viewDir = normalize(u_cameraPosition.xyz - v_worldPos);
	vec3 lightDir = normalize(v_lightDirection);// This should be a uniform, not v_lightDirection
	vec3 halfDir = normalize(viewDir + lightDir);

	float shininess = pow(512.0, smoothness);// USE smoothness variable!
	float specularStrength = pow(max(dot(normal, halfDir), 0.0), shininess);

	// Specular color
	vec3 specularColor = mix(vec3(0.04), baseColor.rgb, metallic);// USE metallic variable!
	vec3 specular = specularColor * specularStrength;


	#ifdef shadowMapFlag
	gl_FragColor.rgb = (baseColor.rgb * (v_ambientLight + shadow * v_lightDiffuse) + specular)  + emissive.rgb;
	#else
	gl_FragColor.rgb = (baseColor.rgb * (v_ambientLight + v_lightDiffuse) + specular)  + emissive.rgb;
	#endif

	#ifdef fogFlag
	gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fog);
	#endif

	gl_FragColor.a = baseColor.a;
}
