#version 330

uniform sampler2D texture_diffuse;

uniform vec3 viewPosition;

struct DirLight {
	vec3 direction;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};
uniform DirLight dirLight;

struct PointLight {
	//float constant = 1.0;
	//float linear = 0.14;
	//float quadratic = 0.07;
	
	vec3 position;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};
#define POINTLIGHTS 4  
uniform PointLight pointLights[POINTLIGHTS];

uniform float gamma;

in vec4 passColour;
in vec3 passNormal;
in vec2 passTexcoord;
in vec3 fragPosition;

out vec4 outColour;

vec3 calculateDirLight(DirLight light, vec3 normal, vec3 viewDirection) {
	vec3 lightDirection = normalize(-light.direction);
	
	float diff = max(dot(normal, lightDirection), 0.0);
	
	vec3 reflectDirection = reflect(-lightDirection, normal);
	float spec = pow(max(dot(viewDirection, reflectDirection), 0.0), 32);
	
	vec3 ambient = light.ambient;
	vec3 diffuse = light.diffuse * diff;
	vec3 specular = light.specular * spec;
	
	return (ambient + diffuse + specular);
}

vec3 calculatePointLight(PointLight light, vec3 normal, vec3 fragPosition, vec3 viewDirection) {
	float constant = 1.0;
	float linear = 0.14;
	float quadratic = 0.07;

	vec3 lightDirection = normalize(light.position - fragPosition);
	vec3 halfwayDirection = normalize(lightDirection + viewDirection);
	
	float diff = max(dot(normal, lightDirection), 0.0);
	
	float spec = pow(max(dot(normal, halfwayDirection), 0.0), 32);
	
	float dist = length(light.position - fragPosition);
	float attenuation = 1.0 / (constant + linear * dist + 
							quadratic * (dist * dist));
	
	vec3 ambient = light.ambient;
	vec3 diffuse = light.diffuse * diff;
	vec3 specular = light.specular * spec;
	ambient *= attenuation;
	diffuse *= attenuation;
	specular *= attenuation;
	
	return (ambient + diffuse + specular);
}

vec4 gammaCorrection(vec4 fragColour) {
	fragColour.rgb = pow(fragColour.rgb, vec3(1.0/gamma));
	return fragColour;
}

void main(void) {
	vec3 normal = normalize(passNormal);
	vec3 viewDirection = normalize(viewPosition - fragPosition);

	vec3 result = calculateDirLight(dirLight, normal, viewDirection);
	
	for (int i=0; i<POINTLIGHTS; i++) {
		result += calculatePointLight(pointLights[i], normal, fragPosition, viewDirection);
	}	
	
	vec4 fragColour = texture(texture_diffuse, passTexcoord);

	outColour = fragColour * vec4(result, 1.0);
	
	outColour = gammaCorrection(outColour);
}