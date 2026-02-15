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


varying vec4 v_color;
varying float v_alphaTest;
varying MED vec2 v_UV;
uniform sampler2D u_leafTexture;
uniform sampler2D u_trunkTexture;// NEW: Trunk texture

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
void main() {
	vec4 diffuse = getTexture();
	if (diffuse.a < 0.25) discard;
	gl_FragColor = diffuse;
}
