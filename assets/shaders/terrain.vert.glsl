attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
attribute vec3 a_biomeData;// x=primary slot, y=secondary slot, z=blend

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

varying vec3 v_normal;
varying vec2 v_texCoord;
varying vec3 v_biomeData;

void main() {
    vec4 worldPos = u_worldTrans * vec4(a_position, 1.0);
    gl_Position = u_projViewTrans * worldPos;

    v_normal = normalize(mat3(u_worldTrans) * a_normal);
    v_texCoord = a_texCoord0;
    v_biomeData = a_biomeData;
}