// ============================================================
// WIND SYSTEM - Complete GLSL Implementation
// Ported from Unity Shader Graph
// ============================================================
//
// Vertex Color Channels:
//   R = Height gradient (wind mask / gale bend)
//   G = Leaf tip gradient
//   B = Leaf mask (breeze mask)
//   A = All
//
// Usage: call applyWindSystem() in your vertex shader to get
//        the final displaced object-space position.
// ============================================================

// ===================== UNIFORMS =============================

// Global
uniform float u_time;
uniform float u_objectYRotationDeg;
uniform bool  u_useGlobalWeatherController;
uniform vec3  u_windDirection;
uniform float u_galeStrength;
uniform float u_windIntensity;
uniform bool  u_useVertexColorWind;

// Gale bend
uniform float u_galeBend;

// Strong wind
uniform float u_strongWindFrequency;
uniform float u_strongWindStrength;
uniform bool  u_enableStrongWind;

// Wind twist
uniform float u_windTwistStrength;
uniform bool  u_enableWindTwist;

// Light wind
uniform float u_lightWindYOffset;
uniform float u_lightWindYStrength;
uniform float u_lightWindStrength;
uniform bool  u_enableLightWind;
uniform bool  u_lightWindUseLeafFade;

// Breeze
uniform float u_breezeStrength;
uniform bool  u_enableBreeze;


// ===================== HELPERS ==============================

vec3 rotateAboutAxis(vec3 pos, vec3 axis, float angleRad) {
	axis = normalize(axis);
	float s  = sin(angleRad);
	float c  = cos(angleRad);
	float oc = 1.0 - c;
	mat3 rot = mat3(
	oc * axis.x * axis.x + c, oc * axis.x * axis.y - axis.z * s, oc * axis.x * axis.z + axis.y * s,
	oc * axis.y * axis.x + axis.z * s, oc * axis.y * axis.y + c, oc * axis.y * axis.z - axis.x * s,
	oc * axis.z * axis.x - axis.y * s, oc * axis.z * axis.y + axis.x * s, oc * axis.z * axis.z + c
	);
	return rot * pos;
}

// Hash-based 2D noise. Replace with your engine's Perlin/Simplex
// for smoother results matching Unity's Simple Noise node.
float hash(vec2 p) {
	vec3 p3 = fract(vec3(p.xyx) * 0.13);
	p3 += dot(p3, p3.yzx + 3.333);
	return fract((p3.x + p3.y) * p3.z);
}

float simpleNoise(vec2 uv, float scale) {
	vec2 p = uv * scale;
	vec2 i = floor(p);
	vec2 f = fract(p);
	vec2 u = f * f * (3.0 - 2.0 * f);// smoothstep

	float a = hash(i);
	float b = hash(i + vec2(1.0, 0.0));
	float c = hash(i + vec2(0.0, 1.0));
	float d = hash(i + vec2(1.0, 1.0));

	return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}


// ============================================================
// 1. WIND DIRECTION
//    Counter-rotate wind direction to undo object Y rotation
// ============================================================

vec3 getLocalWindDirection() {
	vec3 windDir = u_useGlobalWeatherController ? u_windDirection : vec3(1.0, 0.0, 0.0);

	// Rotate about Y to counter object rotation
	float angle = radians(u_objectYRotationDeg);
	float s = sin(angle);
	float c = cos(angle);
	vec3 rotated = vec3(
	windDir.x * c + windDir.z * s,
	windDir.y,
	-windDir.x * s + windDir.z * c
	);

	// Flatten to XZ plane
	return rotated * vec3(1.0, 0.0, 1.0);
}


// ============================================================
// 2. GALE STRENGTH
//    Master control from weather or fallback 0.2
//    Remapped [0,1] -> [0.25,1] for breeze / light wind
// ============================================================

float getGaleStrength() {
	return u_useGlobalWeatherController ? u_galeStrength : 0.2;
}

float getGaleRemap(float gale) {
	return mix(0.25, 1.0, clamp(gale, 0.0, 1.0));
}


// ============================================================
// 3. BEND MESH TO GALE
//    Oscillating bend: gale * 20 (oscillating) + gale * 50 (constant)
//    Blended by vertexColor.R * u_galeBend
// ============================================================

vec3 bendMeshToGale(vec3 objectPos, vec3 windAxis, float gale, float vertexColorR) {
	float oscillation = sin(u_time) * 0.5 + 0.5;

	float oscillatingBend = oscillation * gale * 20.0;
	float constantBend    = gale * 50.0;
	float rotationDeg     = oscillatingBend + constantBend;

	vec3 rotated = rotateAboutAxis(objectPos, windAxis, radians(rotationDeg));

	float t = vertexColorR * u_galeBend;
	return mix(objectPos, rotated, t);
}


// ============================================================
// 4. STRONG WIND WITH TWIST
//    Sine-driven sway + Y-axis twist, scaled by gale * 2
// ============================================================

vec3 strongWindWithTwist(vec3 objectPos, vec3 windDir, float gale) {
	float galeScale = gale * 2.0;
	float posX = objectPos.x;

	// --- Strong wind sway ---
	float swayPhase = (posX + 2.0) * 1.5 * u_strongWindFrequency * u_time;
	float sway = sin(swayPhase) * u_strongWindStrength * galeScale;

	// Displace along wind direction with X zeroed
	vec3 windDisplace = sway * vec3(0.0, windDir.y, windDir.z);
	windDisplace = u_enableStrongWind ? windDisplace : vec3(0.0);

	// --- Wind twist ---
	float twistPhase = (posX + u_time) * u_strongWindFrequency * 5.0;
	float twistAngle = sin(twistPhase) * u_windTwistStrength * 40.0;
	float finalTwist = u_enableWindTwist ? twistAngle : 0.0;

	// Combine
	vec3 displaced = objectPos + windDisplace;
	return u_enableWindTwist
	? rotateAboutAxis(displaced, vec3(0.0, 1.0, 0.0), radians(finalTwist))
	: displaced;
}


// ============================================================
// 5. LIGHT WIND
//    Noise-driven subtle displacement
//    Scaled by galeRemap * 0.2
// ============================================================

vec3 lightWind(vec3 worldPos, vec3 worldNormal, vec4 vertexColor, float gale) {
	if (!u_enableLightWind) return vec3(0.0);

	// Gale influence: remap [0,1]->[0.25,1], then * 0.2
	float galeInfluence = getGaleRemap(gale) * 0.2;

	// Scrolling noise UVs from world XZ
	vec2 posXZ = worldPos.xz * 0.01;
	vec2 scrollOffset = u_time * 0.2 * vec2(-1.0, -1.0);
	vec2 noiseUV = posXZ + scrollOffset;

	// Y offset contribution
	float yFade = (1.0 - u_lightWindYOffset) * 0.5;
	noiseUV += vec2(yFade);

	// Sample noise and remap [0,1] -> [-1,1]
	float noise = simpleNoise(noiseUV, 50.0);
	float remapped = (noise - 0.5) * 2.0;

	// Displacement direction: world normal scaled by Y strength
	vec3 displaceDir = worldNormal * vec3(1.0, u_lightWindYStrength, 1.0);

	// Scale displacement
	vec3 displacement = remapped * displaceDir * 3.0 * u_lightWindStrength * galeInfluence;

	// Leaf fade mask from vertex color R
	float mask = u_lightWindUseLeafFade ? vertexColor.r : 1.0;
	displacement *= mask;

	// Zero out X component
	displacement.x = 0.0;

	return displacement;
}


// ============================================================
// 6. BREEZE
//    Larger-scale noise displacement
//    Uses galeRemap for strength modulation
// ============================================================

vec3 breeze(vec3 worldPos, vec3 worldNormal, vec4 vertexColor, float gale) {
	if (!u_enableBreeze) return vec3(0.0);

	// Gale drives breeze remap [0,1] -> [0.25,1]
	float galeRemap = getGaleRemap(gale);

	// Scrolling noise UVs from world XZ
	vec2 posXZ = worldPos.xz * 0.01;
	vec2 scrollOffset = u_time * 0.01 * vec2(1.0, 1.0);
	vec2 noiseUV = posXZ + scrollOffset;

	// Sample noise and remap [0,1] -> [-1,1]
	float noise = simpleNoise(noiseUV, 100.0);
	float remapped = (noise - 0.5) * 2.0;

	// Strength: base [2,4] range * gale remap
	float strength = mix(2.0, 4.0, u_breezeStrength) * galeRemap;

	// Displacement direction: world normal with Y zeroed
	vec3 displaceDir = worldNormal * vec3(1.0, 0.0, 1.0);

	// Final displacement
	vec3 displacement = remapped * displaceDir * strength;

	// Mask by vertex color Blue channel
	displacement *= vertexColor.b;

	// Zero out X component
	displacement.x = 0.0;

	return displacement;
}


// ============================================================
// MAIN ENTRY POINT
// ============================================================
//
// Call from your vertex shader:
//
//   vec3 objectPos   = position.xyz;             // object-space vertex pos
//   vec3 worldPos    = (modelMatrix * vec4(objectPos, 1.0)).xyz;
//   vec3 worldNormal = normalize(normalMatrix * normal);
//   vec4 vColor      = vertexColor;              // RGBA vertex colors
//
//   vec3 windPos = applyWindSystem(objectPos, worldPos, worldNormal, vColor);
//   gl_Position  = projectionMatrix * viewMatrix * modelMatrix * vec4(windPos, 1.0);
//
// ============================================================

vec3 applyWindSystem(
vec3 objectPos,
vec3 worldPos,
vec3 worldNormal,
vec4 vertexColor
) {
	// 1. Master gale strength
	float gale = getGaleStrength();

	// 2. Local wind direction (counter-rotated)
	vec3 windAxis = getLocalWindDirection();

	// 3. Gale bend (large-scale oscillating mesh bending)
	vec3 bentPos = bendMeshToGale(objectPos, windAxis, gale, vertexColor.r);

	// 4. Strong wind sway + twist
	vec3 strongWindPos = strongWindWithTwist(bentPos, windAxis, gale);
	vec3 strongWindDisplacement = strongWindPos - bentPos;

	// 5. Light wind (additive noise displacement)
	vec3 lightWindDisplacement = lightWind(worldPos, worldNormal, vertexColor, gale);

	// 6. Breeze (additive noise displacement)
	vec3 breezeDisplacement = breeze(worldPos, worldNormal, vertexColor, gale);

	// 7. Combine all
	vec3 combined = bentPos + strongWindDisplacement + lightWindDisplacement + breezeDisplacement;

	// 8. Wind mask: vertex color R (height gradient) or objectPos.y * 0.5
	float windMask = u_useVertexColorWind
	? vertexColor.r
	: objectPos.y * 0.5;

	// 9. Lerp: original -> wind-displaced by mask
	vec3 masked = mix(objectPos, combined, windMask);

	// 10. Master intensity: from weather or fallback 0.2
	float intensity = u_useGlobalWeatherController
	? clamp(u_windIntensity, 0.0, 1.0)
	: 0.2;

	// 11. Final lerp: original -> masked by intensity
	return mix(objectPos, masked, intensity);
}
