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
uniform vec3 u_renderOffset;

varying vec3 v_worldPos;
varying vec3 v_normal;
varying vec2 v_tiledUV;
varying vec2 v_splatUV;
varying vec3 v_viewDir;
varying vec3 v_tangent;
varying vec3 v_bitangent;

void main() {
    vec4 worldPos = u_worldTrans * vec4(a_position, 1.0);

    // Apply floating origin offset
    worldPos.xyz -= u_renderOffset;

    v_worldPos = worldPos.xyz;

    // Transform normal to world space
    mat3 normalMatrix = mat3(u_worldTrans[0].xyz, u_worldTrans[1].xyz, u_worldTrans[2].xyz);
    vec3 normal = normalize(normalMatrix * a_normal);
    v_normal = normal;

    // Calculate tangent and bitangent for terrain
    // For terrain on XZ plane, tangent follows X axis, bitangent follows Z axis
    vec3 worldTangent = normalize(normalMatrix * vec3(1.0, 0.0, 0.0));
    vec3 worldBitangent = normalize(normalMatrix * vec3(0.0, 0.0, 1.0));

    // Gram-Schmidt orthogonalization - make tangent perpendicular to normal
    worldTangent = normalize(worldTangent - dot(worldTangent, normal) * normal);

    // Recalculate bitangent to ensure orthogonal basis
    worldBitangent = cross(normal, worldTangent);

    v_tangent = worldTangent;
    v_bitangent = worldBitangent;

    v_tiledUV = a_texCoord0;
    v_splatUV = a_texCoord1;
    v_viewDir = normalize(u_cameraPosition - worldPos.xyz);

    gl_Position = u_projViewTrans * worldPos;
}