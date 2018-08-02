#version 330 core

layout (location = 0) in vec4 inPosition;
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec4 inColour;
layout (location = 3) in vec2 inTexcoord;

out VS_OUT {
	vec2 texcoord;
} vs_out;

uniform mat4 model;

void main() {
	gl_Position = model * inPosition;
	
	vs_out.texcoord = inTexcoord;
}