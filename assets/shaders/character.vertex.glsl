attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
attribute vec4 a_boneWeight0;
attribute vec4 a_boneWeight1;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat4 u_bones[12];// Back to 12

varying vec2 v_texCoord0;
varying vec3 v_normal;

void main() {
    int bone0 = int(min(a_boneWeight0.y, 11.0));
    int bone1 = int(min(a_boneWeight0.w, 11.0));
    int bone2 = int(min(a_boneWeight1.y, 11.0));
    int bone3 = int(min(a_boneWeight1.w, 11.0));

    float weight0 = a_boneWeight0.x;
    float weight1 = a_boneWeight0.z;
    float weight2 = a_boneWeight1.x;
    float weight3 = a_boneWeight1.z;

    float totalWeight = weight0 + weight1 + weight2 + weight3;

    vec4 skinnedPos;
    vec3 skinnedNormal;

    if (totalWeight > 0.0) {
        // Apply skinning with weights
        skinnedPos = vec4(0.0);
        skinnedPos += u_bones[bone0] * vec4(a_position, 1.0) * weight0;
        skinnedPos += u_bones[bone1] * vec4(a_position, 1.0) * weight1;
        skinnedPos += u_bones[bone2] * vec4(a_position, 1.0) * weight2;
        skinnedPos += u_bones[bone3] * vec4(a_position, 1.0) * weight3;

        skinnedNormal = vec3(0.0);
        skinnedNormal += mat3(u_bones[bone0]) * a_normal * weight0;
        skinnedNormal += mat3(u_bones[bone1]) * a_normal * weight1;
        skinnedNormal += mat3(u_bones[bone2]) * a_normal * weight2;
        skinnedNormal += mat3(u_bones[bone3]) * a_normal * weight3;
    } else {
        // No weights - transform by first bone index (rigid skinning)
        skinnedPos = u_bones[bone0] * vec4(a_position, 1.0);
        skinnedNormal = mat3(u_bones[bone0]) * a_normal;
    }

    gl_Position = u_projViewTrans * u_worldTrans * skinnedPos;
    v_texCoord0 = a_texCoord0;
    v_normal = normalize(mat3(u_worldTrans) * skinnedNormal);
}