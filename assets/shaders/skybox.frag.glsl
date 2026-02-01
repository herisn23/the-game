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
varying vec3 v_texCoord;
uniform samplerCube u_cubemap;

void main() {
    gl_FragColor = textureCube(u_cubemap, v_texCoord);
}