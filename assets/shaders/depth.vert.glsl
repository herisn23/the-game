attribute vec3 a_position;
uniform mat4 u_projViewWorldTrans;

attribute vec2 a_texCoord0;
varying vec2 v_texCoords0;

attribute vec3 a_normal;

#if defined(colorFlag)
attribute vec4 a_color;
#endif

#ifdef boneWeight0Flag
#define boneWeightsFlag
attribute vec2 a_boneWeight0;
#endif//boneWeight0Flag

#ifdef boneWeight1Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight1;
#endif//boneWeight1Flag

#ifdef boneWeight2Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight2;
#endif//boneWeight2Flag

#ifdef boneWeight3Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight3;
#endif//boneWeight3Flag

#ifdef boneWeight4Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight4;
#endif//boneWeight4Flag

#ifdef boneWeight5Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight5;
#endif//boneWeight5Flag

#ifdef boneWeight6Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight6;
#endif//boneWeight6Flag

#ifdef boneWeight7Flag
#ifndef boneWeightsFlag
#define boneWeightsFlag
#endif
attribute vec2 a_boneWeight7;
#endif//boneWeight7Flag

#if defined(numBones) && defined(boneWeightsFlag)
#if (numBones > 0)
#define skinningFlag
#endif
#endif

#if defined(numBones)
#if numBones > 0
uniform mat4 u_bones[numBones];
#endif//numBones
#endif

#ifdef PackedDepthFlag
varying float v_depth;
#endif//PackedDepthFlag

#ifdef shiftFlag
uniform vec3 u_shiftOffset;
#endif

// Wind needs these separately
#ifdef windFlag
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform mat3 u_normalMatrix;
#endif

uniform float u_shadowBias;

void main() {
    v_texCoords0 = a_texCoord0;

    #ifdef skinningFlag
    mat4 skinning = mat4(0.0);
    #ifdef boneWeight0Flag
    skinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];
    #endif
    #ifdef boneWeight1Flag
    skinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];
    #endif
    #ifdef boneWeight2Flag
    skinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];
    #endif
    #ifdef boneWeight3Flag
    skinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];
    #endif
    #ifdef boneWeight4Flag
    skinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];
    #endif
    #ifdef boneWeight5Flag
    skinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];
    #endif
    #ifdef boneWeight6Flag
    skinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];
    #endif
    #ifdef boneWeight7Flag
    skinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];
    #endif
    #endif

    // ========================================================
    // WIND PATH: needs separate world transform
    // ========================================================
    #ifdef windFlag

    #ifdef skinningFlag
    vec3 objectPos = (skinning * vec4(a_position, 1.0)).xyz;
    #else
    vec3 objectPos = a_position;
    #endif

    vec3 worldPos = (u_worldTrans * vec4(objectPos, 1.0)).xyz;
    vec3 normal = normalize(u_normalMatrix * a_normal);

    #ifdef shiftFlag
    worldPos -= u_shiftOffset;
    #endif

    #ifdef colorFlag
    vec4 windVertexColor = a_color;
    #else
    vec4 windVertexColor = vec4(1.0);
    #endif

    vec3 windPos = applyWindSystem(objectPos, worldPos, normal, windVertexColor);
    vec4 pos = u_worldTrans * vec4(windPos, 1.0);

    #ifdef shiftFlag
    pos.xyz -= u_shiftOffset;
    #endif

    pos = u_projViewTrans * pos;

    // ========================================================
    // NON-WIND PATH: original combined matrix
    // ========================================================
    #else

    vec4 pos = vec4(a_position, 1.0);

    #ifdef shiftFlag
    pos.xyz -= u_shiftOffset;
    #endif

    #ifdef skinningFlag
    pos = u_projViewWorldTrans * skinning * pos;
    #else
    pos = u_projViewWorldTrans * pos;
    #endif

    #endif// windFlag

    #ifdef PackedDepthFlag
    v_depth = pos.z / pos.w * 0.5 + 0.5;
    #endif

    gl_Position = pos;
}