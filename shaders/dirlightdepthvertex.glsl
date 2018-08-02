#version 330

layout (location = 0) in vec4 inPosition;
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec4 inColour;
layout (location = 3) in vec2 inTexcoord;

out vec2 passTexcoord;

uniform mat4 model;
uniform mat4 lightMatrix;

void main(void) {
	gl_Position = lightMatrix * model * inPosition;
	
	passTexcoord = inTexcoord;
}