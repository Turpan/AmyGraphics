#version 330 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 18) out;

uniform mat4 lightMatrices[6];

in VS_OUT {
	vec2 texcoord;
} gs_in[];

out vec4 fragPosition;
out vec2 passTexcoord;

void main() {
	for (int face = 0; face < 6; ++face) {
		gl_Layer = face;
		for (int i=0; i<3; ++i) {
			passTexcoord = gs_in[i].texcoord;
			fragPosition = gl_in[i].gl_Position;
			gl_Position = lightMatrices[face] * fragPosition;
			EmitVertex();
		}
		EndPrimitive();
	}
}