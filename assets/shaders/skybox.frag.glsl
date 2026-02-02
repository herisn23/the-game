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
uniform samplerCube u_dayCubemap;
uniform samplerCube u_nightCubemap;
uniform float u_blend;// 0 = day, 1 = night
uniform vec3 u_dayTint;// tint color for day (sunset/sunrise)
uniform float u_brightness;// overall brightness
uniform float u_nightRotation;// Add this - rotation angle in radians

varying vec3 v_texCoord;

// Rotate around Y axis (vertical)
vec3 rotateX(vec3 v, float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return vec3(
    v.x,
    v.y * c - v.z * s,
    v.y * s + v.z * c
    );
}

vec3 rotateY(vec3 v, float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return vec3(
    v.x * c + v.z * s,
    v.y,
    -v.x * s + v.z * c
    );
}

vec3 rotateZ(vec3 v, float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return vec3(
    v.x * c - v.y * s,
    v.x * s + v.y * c,
    v.z
    );
}

void main() {
    // Day - no rotation
    vec4 dayColor = textureCube(u_dayCubemap, v_texCoord);
    dayColor.rgb *= u_dayTint * u_brightness;

    // Night - rotated
    vec3 nightCoord = rotateZ(v_texCoord, u_nightRotation);
    vec4 nightColor = textureCube(u_nightCubemap, nightCoord);

    gl_FragColor = mix(dayColor, nightColor, u_blend);
}