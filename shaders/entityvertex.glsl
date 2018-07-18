#version 330

layout (location = 0) in vec4 inPosition;
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec4 inColour;
layout (location = 3) in vec2 inTexcoord;

out vec4 passColour;
out vec3 passNormal;
out vec2 passTexcoord;
out vec3 fragPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main(void) {
	gl_Position = projection * view * model * inPosition;
	
	passColour = inColour;
	passNormal = vec3(vec4(inNormal, 0.0) * model);
	//passNormal = inNormal;
	passTexcoord = inTexcoord;
	fragPosition = vec3(model * inPosition);
}