// ===================== UNIFORMS =============================

// Global
uniform float u_posScale;
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


// ===== Functions ========
float remap(float value, float fromLow, float fromHigh, float toLow, float toHigh) {
	return toLow + (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow);
}
vec3 rotateAboutAxis(vec3 pos, vec3 axis, float rotation) {
	float angleRad = radians(rotation);// built-in GLSL function
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
vec2 gradientDir(vec2 p) {
	float h = dot(p, vec2(127.1, 311.7));
	vec2 g = fract(sin(vec2(h, h + 113.5)) * 43758.5453);
	return normalize(g * 2.0 - 1.0);
}

float simpleNoise(vec2 uv, float scale) {
	vec2 p = uv * scale;
	vec2 i = floor(p);
	vec2 f = fract(p);
	vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);// quintic smoothstep

	float a = dot(gradientDir(i), f);
	float b = dot(gradientDir(i + vec2(1.0, 0.0)), f - vec2(1.0, 0.0));
	float c = dot(gradientDir(i + vec2(0.0, 1.0)), f - vec2(0.0, 1.0));
	float d = dot(gradientDir(i + vec2(1.0, 1.0)), f - vec2(1.0, 1.0));

	float result = mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
	return result * 0.5 + 0.5;// remap to 0..1 to match Unity
}

// === Bend meshes to gale strength and oscillate the bend ===
vec3 getGaleBend(
vec3 windDirection,
vec3 objectPos,
vec4 vertexColor,
float galeStrength
) {
	vec3 rotatedWindDiretion = rotateAboutAxis(windDirection, vec3(0.0, 1.0, 0.0), u_objectYRotationDeg) * vec3(1.0, 0.0, 1.0);
	float strengthInTime = ((sin(u_time) * 0.5 + 0.5) * (galeStrength * 20.0)) + (galeStrength * 50.0);
	vec3 rotatedPosition = rotateAboutAxis(objectPos, rotatedWindDiretion, strengthInTime);
	return mix(objectPos, rotatedPosition, vertexColor.r * u_galeBend);
}

// ==== STRONG WIND AND TWIST
vec3 getStrongWindWithTwist(vec3 galeBent, vec3 windDirection, vec3 objectPos, float galeStrength) {
	vec3 windDirFreq = vec3(0.0);
	float posInTime = u_time + objectPos.x + objectPos.z;

	if (u_enableStrongWind) {
		float freq = (galeStrength + 2.0) * u_strongWindFrequency * 1.5;
		windDirFreq = windDirection * sin(freq * posInTime) * u_strongWindStrength * 5.0;
	}

	vec3 finalWindDir = windDirFreq + galeBent;

	if (u_enableWindTwist) {
		float rotation = sin(posInTime * u_strongWindFrequency) * (u_windTwistStrength * 40.0);
		finalWindDir = rotateAboutAxis(finalWindDir, vec3(0.0, 1.0, 0.0), rotation);
	}

	return finalWindDir;
}
// ===== LIGHT WIND =======
vec3 getLightWind(
vec3 windDirection,
vec3 worldPos,
vec3 worldNormal,
vec4 vertColor,
float galeStrength
) {
	if (!u_enableLightWind) {
		return vec3(0.0);
	}
	// [verified]
	float galeStrengthInTime = remap(galeStrength, 0.0, 1.0, 0.25, 1.0) * u_time * 0.05;
	vec2 windDirInTime = windDirection.xz * vec2(-1.0) * galeStrengthInTime;
	vec2 pos = worldPos.xz * vec2(0.01);
	vec2 posAndDir = pos + windDirInTime;
	// [/verified]

	float yOffset = (1.0 - u_lightWindYOffset) * 0.5;
	float yNoiseOffset = (simpleNoise(posAndDir, 50.0) - yOffset) * 2.0;
	vec3 yStrength = vec3(1.0, u_lightWindStrength, 1.0) * worldNormal;
	float multipliedStrength = u_lightWindStrength * 3.0;
	float leafFade = u_lightWindUseLeafFade
	? vertColor.g
	: vertColor.b;
	return leafFade * yNoiseOffset * yStrength * multipliedStrength;
}


// ===== BREEZE =======
vec3 getBreeze(
vec3 windDirection,
vec3 worldPos,
vec3 worldNormal,
vec4 vertColor,
float galeStrength
) {
	if (!u_enableBreeze)
	return vec3(0.0);
	vec2 dirInTime = windDirection.xz * u_time * 0.003;
	vec2 posInTime = worldPos.xz * vec2(0.01);
	vec2 dirPos = dirInTime + posInTime;
	vec3 noise = (simpleNoise(dirPos, 1000.0) - 0.5) * 2.0 * worldNormal;
	return remap(galeStrength, 0.0, 1.0, 2.0, 4.0) * vec3(u_breezeStrength, 0.0, u_breezeStrength) * noise * vertColor.g;
}

vec3 applyWindSystem(
mat4 worldTrans,
vec3 objectPos,
vec3 worldPos,
vec3 worldNormal,
vec4 vertexColor
) {
	objectPos *= u_posScale;// downscale for constant for unity
	vec3 objectPivotPos = worldTrans[3].xyz;

	vec3 windDirection = u_useGlobalWeatherController
	? u_windDirection
	: vec3(1.0, 0.0, 0.0);
	float galeStrength = u_useGlobalWeatherController
	? u_galeStrength
	: 0.2;

	vec3 galeBend = getGaleBend(windDirection, objectPos, vertexColor, galeStrength);
	vec3 strongWind = getStrongWindWithTwist(galeBend, windDirection, objectPivotPos, galeStrength);
	vec3 lightWind = getLightWind(windDirection, worldPos, worldNormal, vertexColor, galeStrength);
	vec3 breeze = getBreeze(windDirection, worldPos, worldNormal, vertexColor, galeStrength);



	vec3 wind = strongWind + lightWind + breeze;

	float windMask = u_useVertexColorWind
	? vertexColor.r
	: objectPos.y * 0.5;// T(3)

	vec3 mask = mix(galeBend, wind, windMask);


	float intensity = u_useGlobalWeatherController
	? clamp(u_windIntensity, 0.0, 1.0)
	: 0.2;

	return mix(objectPos, mask, intensity) / u_posScale;
}
