#ifdef GL_ES
precision mediump float;
#endif

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;// Tiled UV
attribute vec2 a_texCoord1;// Splatmap UV

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec3 u_cameraPosition;
uniform vec3 u_renderOffset;// Add this

varying vec3 v_worldPos;
varying vec3 v_normal;
varying vec2 v_tiledUV;
varying vec2 v_splatUV;
varying vec3 v_viewDir;

void main() {
    vec4 worldPos = u_worldTrans * vec4(a_position, 1.0);

    // Apply floating origin offset
    worldPos.xyz -= u_renderOffset;

    v_worldPos = worldPos.xyz;

    v_normal = normalize(mat3(u_worldTrans[0].xyz, u_worldTrans[1].xyz, u_worldTrans[2].xyz) * a_normal);
    v_tiledUV = a_texCoord0;
    v_splatUV = a_texCoord1;
    v_viewDir = normalize(u_cameraPosition - worldPos.xyz);

    gl_Position = u_projViewTrans * worldPos;
}