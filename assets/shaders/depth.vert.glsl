attribute vec3 a_position;
uniform mat4 u_projViewWorldTrans;

#if defined(diffuseTextureFlag) && defined(blendedFlag)
#define blendedTextureFlag
attribute vec2 a_texCoord0;
varying vec2 v_texCoords0;
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

#ifdef windFlag
// Wind uniforms
uniform float u_time;
uniform float u_windStrength;
uniform float u_windSpeed;
uniform vec2 u_windDirection;
#endif

uniform float u_shadowBias;// Typically 0.001 - 0.01

void main() {
    #ifdef blendedTextureFlag
    v_texCoords0 = a_texCoord0;
    #endif// blendedTextureFlag

    #ifdef skinningFlag
    mat4 skinning = mat4(0.0);
    #ifdef boneWeight0Flag
    skinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];
    #endif//boneWeight0Flag
    #ifdef boneWeight1Flag
    skinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];
    #endif//boneWeight1Flag
    #ifdef boneWeight2Flag
    skinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];
    #endif//boneWeight2Flag
    #ifdef boneWeight3Flag
    skinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];
    #endif//boneWeight3Flag
    #ifdef boneWeight4Flag
    skinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];
    #endif//boneWeight4Flag
    #ifdef boneWeight5Flag
    skinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];
    #endif//boneWeight5Flag
    #ifdef boneWeight6Flag
    skinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];
    #endif//boneWeight6Flag
    #ifdef boneWeight7Flag
    skinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];
    #endif//boneWeight7Flag
    #endif//skinningFlag

    vec4 pos = vec4(a_position, 1.0);

    #ifdef shiftFlag
    pos.xyz -= u_shiftOffset;
    #endif


    #ifdef windFlag
    // ===== WIND ANIMATION =====
    float windInfluence = clamp(a_position.y, 0.0, 1.0);

    float windTime = u_time * u_windSpeed;
    float variation = sin(pos.x * 0.01) * cos(pos.z * 0.01);

    float sway1 = sin(windTime + pos.x * 0.05 + variation);
    float sway2 = sin(windTime * 0.7 + pos.z * 0.03 - variation);

    // Multiply by 100 to compensate for 0.01 model scale
    vec2 windOffset = vec2(sway1, sway2) * windInfluence * u_windStrength * 100.0;

    pos.x += windOffset.x * u_windDirection.x;
    pos.z += windOffset.y * u_windDirection.y;
    #endif

    #ifdef skinningFlag
    pos = u_projViewWorldTrans * skinning * pos;
    #else
    pos = u_projViewWorldTrans * pos;
    #endif





    #ifdef PackedDepthFlag
    v_depth = pos.z / pos.w * 0.5 + 0.5;
    #endif//PackedDepthFlag

    gl_Position = pos;
}
