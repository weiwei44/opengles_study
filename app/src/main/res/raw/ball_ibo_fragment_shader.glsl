precision mediump float;

uniform sampler2D u_TextureUnit; // 纹理采样器
varying vec2 v_TextureCoordinates; // 纹理坐标

void main() {
//    vec4 vColor = vec4(1.0,0.2,0.8,0);
//    gl_FragColor = vColor;
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}
