#version 330

uniform sampler2D texture_diffuse;

in vec2 passTexcoord;

void main(void) {
	vec4 fragColour = texture(texture_diffuse, passTexcoord);
	
	if (fragColour.a != 1.0) {
		discard;
	}
}