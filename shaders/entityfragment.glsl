#version 330

#define POINTLIGHTS 4

uniform sampler2D texture_diffuse;

uniform sampler2D dirShadowMap;

//uniform samplerCube pointShadowMap[POINTLIGHTS];

uniform samplerCube pointShadowMap;

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

uniform PointLight pointLights[POINTLIGHTS];

uniform float gamma;

uniform float farPlane;

in VS_OUT {
	vec4 colour;
	vec3 normal;
	vec2 texcoord;
	vec3 fragPosition;
	vec4 dirLightSpacePosition;
} fs_in;

in vec4 passColour;
in vec3 passNormal;
in vec2 passTexcoord;
in vec3 fragPosition;

out vec4 outColour;

float dirLightShadow(vec4 dirLightFragPosition, vec3 normal, vec3 lightDirection) {
	vec3 projCoords = dirLightFragPosition.xyz / dirLightFragPosition.w;
	
	if (projCoords.z > 1.0) {
		return 0.0;
	}
	
	projCoords = (projCoords * 0.5) + 0.5;
	
	float currentDepth = projCoords.z;
	
	float bias = max(0.05 * (1.0 - dot(normal, lightDirection)), 0.005);
	
	float shadow = 0.0;
	vec2 texelSize = 1.0 / textureSize(dirShadowMap, 0);
	
	for (int x=-1; x <= 1; ++x) {
		for (int y=-1; y<=1; ++y) {
			float pcfDepth = texture(dirShadowMap, projCoords.xy + (vec2(x, y) * texelSize)).r;
			shadow += (currentDepth - bias) > pcfDepth ? 1.0 : 0.0;
		}
	}
	
	shadow /= 9.0;
	
	return shadow;
	
}

float pointLightShadow(vec3 fragPosition, int i) {
	vec3 fragToLight = fragPosition - pointLights[i].position;
	
	float closestDepth = texture(pointShadowMap, fragToLight).r;
	closestDepth *= farPlane;
	
	float currentDepth = length(fragToLight);
	
	float bias = 0.05;
	float shadow = 0.0;
	float samples = 4.0;
	float offset = 0.05;
	
	for (float x = -offset; x < offset; x += offset / (samples * 0.5)) {
		for (float y = -offset; y < offset; y += offset / (samples * 0.5)) {
			for (float z = -offset; z < offset; z += offset / (samples * 0.5)) {
				float closestDepth = texture(pointShadowMap, fragToLight + vec3(x, y, z)).r;
				closestDepth *= farPlane;
				
				if ((currentDepth - bias) > closestDepth) {
					shadow += 1.0;
				}
			}
		}
	}
	
	shadow /= (samples * samples * samples);
	
	return shadow;
}

vec3 calculateDirLight(DirLight light, vec3 normal, vec3 viewDirection) {
	vec3 lightDirection = normalize(-light.direction);
	
	float diff = max(dot(normal, lightDirection), 0.0);
	
	vec3 reflectDirection = reflect(-lightDirection, normal);
	float spec = pow(max(dot(viewDirection, reflectDirection), 0.0), 32);
	
	vec3 ambient = light.ambient;
	vec3 diffuse = light.diffuse * diff;
	vec3 specular = light.specular * spec;
	
	float shadow = dirLightShadow(fs_in.dirLightSpacePosition, fs_in.normal, lightDirection);
	
	return (ambient + ((1.0 - shadow) * (diffuse + specular)));
}

vec3 calculatePointLight(int i, vec3 normal, vec3 fragPosition, vec3 viewDirection) {
	float constant = 1.0;
	float linear = 0.14;
	float quadratic = 0.07;

	vec3 lightDirection = normalize(pointLights[i].position - fragPosition);
	vec3 halfwayDirection = normalize(lightDirection + viewDirection);
	
	float diff = max(dot(normal, lightDirection), 0.0);
	
	float spec = pow(max(dot(normal, halfwayDirection), 0.0), 32);
	
	float dist = length(pointLights[i].position - fragPosition);
	float attenuation = 1.0 / (constant + linear * dist + 
							quadratic * (dist * dist));
	
	vec3 ambient = pointLights[i].ambient;
	vec3 diffuse = pointLights[i].diffuse * diff;
	vec3 specular = pointLights[i].specular * spec;
	ambient *= attenuation;
	diffuse *= attenuation;
	specular *= attenuation;
	
	float shadow = pointLightShadow(fragPosition, i);
	
	return (ambient + ((1.0 - shadow) * (diffuse + specular)));
}

vec4 gammaCorrection(vec4 fragColour) {
	fragColour.rgb = pow(fragColour.rgb, vec3(1.0/gamma));
	return fragColour;
}

void main(void) {
	vec3 normal = normalize(fs_in.normal);
	vec3 viewDirection = normalize(viewPosition - fs_in.fragPosition);

	vec3 result = calculateDirLight(dirLight, normal, viewDirection);
	
	for (int i=0; i<POINTLIGHTS; i++) {
		result += calculatePointLight(i, normal, fs_in.fragPosition, viewDirection);
	}	
	
	vec4 fragColour = texture(texture_diffuse, fs_in.texcoord);

	outColour = fragColour * vec4(result, 1.0);
	
	outColour = gammaCorrection(outColour);
}