attribute vec3 a_position;
varying vec3 v_texCoord;
uniform mat4 u_projView;

void main() {
    v_texCoord = a_position;
    vec4 pos = u_projView * vec4(a_position, 1.0);
    gl_Position = pos.xyww;
}