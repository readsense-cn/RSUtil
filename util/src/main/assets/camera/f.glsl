#version 300 es
//#extension GL_OES_EGL_image_external_essl3 : require
#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES yuvTexSampler;
in vec2 yuvTexCoords;
out vec4 outColor;
void main() {
    outColor = texture(yuvTexSampler, yuvTexCoords);
//    vec4 vCameraColor = texture(yuvTexSampler,yuvTexCoords);
//    float fGrayColor = (0.3*vCameraColor.r + 0.59*vCameraColor.g + 0.11*vCameraColor.b);
//    outColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
}
