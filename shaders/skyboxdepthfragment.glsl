#version 330 core
out vec4 outColour;

in vec3 passTexcoords;

uniform samplerCube skybox;

void main()
{    
    float depthValue = texture(skybox, passTexcoords).r;
    
    outColour = vec4(vec3(depthValue), 1.0);
}