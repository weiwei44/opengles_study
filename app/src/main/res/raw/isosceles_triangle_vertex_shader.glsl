attribute vec4 vPosition;
uniform mat4 vMatrix;

attribute vec4 aColor;
varying  vec4 vColor;

void main() {
    gl_Position = vMatrix*vPosition;
    vColor=aColor;
}
