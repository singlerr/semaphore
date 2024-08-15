#version 110

varying vec2 f_Position;
varying vec2 texCoord0;
void main() {
    f_Position = gl_Vertex.xy;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_FrontColor = gl_Color;
    texCoord0 = gl_MultiTexCoord0.xy;
}
