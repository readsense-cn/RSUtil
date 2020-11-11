#version 300 es

layout(location=0) in vec4 v_position;
layout(location=1) in vec4 a_texCoord;
uniform mat4 uTextureMatrix;
out vec2 yuvTexCoords;
uniform mat4 u_Matrix;

void main() {
    gl_Position = u_Matrix*v_position;
    //只保留xy分量
    yuvTexCoords = (uTextureMatrix*a_texCoord).xy;
}
