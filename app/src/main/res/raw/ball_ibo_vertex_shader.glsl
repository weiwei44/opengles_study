attribute vec4 vPosition;
uniform mat4 vMatrix;

attribute vec2 a_TextureCoordinates; //纹理坐标
varying vec2 v_TextureCoordinates;

void main() {
    gl_Position =vMatrix*vPosition;
    v_TextureCoordinates = a_TextureCoordinates;
}
