#ifdef GL_ES
precision mediump float;
#endif

varying vec3 v_normal;
varying vec2 v_texCoord;
varying vec3 v_biomeData;

uniform sampler2D u_texture0;
uniform sampler2D u_texture1;
uniform sampler2D u_texture2;
uniform sampler2D u_texture3;

uniform vec3 u_lightDir;
uniform float u_ambientLight;

void main() {
    vec2 uv = fract(v_texCoord);
    int slot = int(v_biomeData.x + 0.5);

    // Sample ALL textures to prevent optimizer removing them
    vec4 t0 = texture2D(u_texture0, uv);
    vec4 t1 = texture2D(u_texture1, uv);
    vec4 t2 = texture2D(u_texture2, uv);
    vec4 t3 = texture2D(u_texture3, uv);

    vec4 color;
    if (slot == 0) color = t0;
    else if (slot == 1) color = t1;
    else if (slot == 2) color = t2;
    else color = t3;

    float light = max(dot(v_normal, -u_lightDir), 0.0) + u_ambientLight;
    gl_FragColor = vec4(color.rgb * light, 1.0);
}