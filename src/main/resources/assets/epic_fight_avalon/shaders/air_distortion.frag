#version 150 core

uniform sampler2D DiffuseSampler;
uniform vec2 ScreenSize;
uniform float Time;
uniform int ParticleCount;
uniform vec4 ParticleData[10];

in vec2 fragTexCoord;
out vec4 fragColor;

void main() {
    fragColor = vec4(1.0, 0.0, 0.0, 1.0);

}