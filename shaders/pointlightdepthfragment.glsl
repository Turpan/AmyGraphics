#version 330 core

uniform sampler2D texture_diffuse;

in vec4 fragPosition;
in vec2 passTexcoord;

uniform vec3 lightPosition;
uniform float farPlane;

void main() {
	vec4 fragColour = texture(texture_diffuse, passTexcoord);
	
	if (fragColour.a <= 0.0) {
		discard;
	}

	float lightDistance = length(fragPosition.xyz - lightPosition);
	
	lightDistance = lightDistance / farPlane;
	
	gl_FragDepth = lightDistance;
}