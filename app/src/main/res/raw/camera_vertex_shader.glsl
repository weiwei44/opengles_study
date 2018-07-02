attribute vec4 vPosition;
attribute vec2 vCoordinate;

uniform mat4 vMatrix;
uniform mat4 vCoordMatrix;

varying  vec2 aCoordinate;

void main() {
    gl_Position = vMatrix*vPosition;

    aCoordinate = (vCoordMatrix*vec4(vCoordinate,0,1)).xy;
}
