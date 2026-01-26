#ifdef GL_ES
precision mediump float;
#endif
varying vec3 v_texCoord;
uniform samplerCube u_cubemap;

void main() {
    gl_FragColor = textureCube(u_cubemap, v_texCoord);
}