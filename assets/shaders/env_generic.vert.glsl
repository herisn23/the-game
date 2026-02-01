#define positionFlag
#define binormalFlag
#define tangentFlag
#define normalFlag
#define lightingFlag
#define ambientCubemapFlag
#define numDirectionalLights 2
#define numPointLights 5
#define numSpotLights 0
#define texCoord0Flag
#define diffuseTextureFlag
#define diffuseTextureCoord texCoord0
#define emissiveTextureFlag
#define emissiveTextureCoord texCoord0
#define diffuseColorFlag
#define emissiveColorFlag
attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat3 u_normalMatrix;
uniform vec3 u_renderOffset;

varying vec2 v_texCoord;
varying vec3 v_normal;

void main() {
    vec4 worldPos = u_worldTrans * vec4(a_position, 1.0);
    // Apply floating origin offset
    worldPos.xyz -= u_renderOffset;
    v_texCoord = a_texCoord0;
    v_normal = normalize(u_normalMatrix * a_normal);
    gl_Position = u_projViewTrans * worldPos;
}