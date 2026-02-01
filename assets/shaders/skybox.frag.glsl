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

varying vec3 v_texCoord;

void main() {
    vec4 dayColor = textureCube(u_dayCubemap, v_texCoord);
    vec4 nightColor = textureCube(u_nightCubemap, v_texCoord);

    // Apply tint and brightness to day
    dayColor.rgb *= u_dayTint * u_brightness;

    // Blend between tinted day and night
    gl_FragColor = mix(dayColor, nightColor, u_blend);
}