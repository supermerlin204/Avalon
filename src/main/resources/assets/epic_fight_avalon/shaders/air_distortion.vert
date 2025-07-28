#version 150 core

in vec3 Position;
in vec2 UV0;

uniform mat4 ProjMat;

out vec2 fragTexCoord;

void main() {
    gl_Position = ProjMat * vec4(Position, 1.0);
    fragTexCoord = UV0;
}