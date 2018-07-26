package amyGLGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector4f;

import movement.Light;

public class GLEntityRenderer extends GLRenderer {
	
	GLEntityProgram entityProgram;
	List<Light> lightSources;
	Map<Light, GLObject> lightMap;
	Light directionalLight;
	GLObject dirLightObject;
	
	public void addLight(Light light, GLObject lightObject) {
		lightSources.add(light);
		lightMap.put(light, lightObject);
	}
	
	public void removeLight(Light light) {
		lightSources.remove(light);
		lightMap.remove(light);
	}
	
	public void setDirectionalLight(Light directionalLight, GLObject dirLightObject) {
		this.directionalLight = directionalLight;
		this.dirLightObject = dirLightObject;
	}

	@Override
	protected void createProgram() {
		entityProgram = new GLEntityProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		entityProgram.updateViewMatrix(camera.getCameraMatrix());
		entityProgram.updateViewPosition(camera.getPosition());
		//TODO this will be setting related, later
		entityProgram.updateGamma(2.2f);
	}

	@Override
	protected void updateUniforms(GLObject object) {
		entityProgram.updateModelMatrix(object.getModelMatrix());
		sendLightData(object);
	}

	@Override
	protected GLProgram getProgram() {
		return entityProgram;
	}
	
	private List<Light> getClosestLights(GLObject entity) {
		Collections.sort(lightSources, (Light l1, Light l2) -> {
			GLObject glLight1 = lightMap.get(l1);
			GLObject glLight2 = lightMap.get(l2);
			
			Vector4f vec1 = glLight1.getVertices().get(0).xyzwVector().mul(entity.getModelMatrix());
			Vector4f vec2 = glLight2.getVertices().get(0).xyzwVector().mul(entity.getModelMatrix());
			Vector4f envec = entity.getVertices().get(0).xyzwVector().mul(entity.getModelMatrix());
			
			var distance1 = envec.distance(vec1);
			var distance2 = envec.distance(vec2);
			
			return (int) (distance2 - distance1);
		});
		
		List<Light> lights = new ArrayList<Light>();
		
		int i = 0;
		for (var light : lightSources) {
			lights.add(light);
			i++;
			if (i == GLRoomHandler.POINTLIGHTCOUNT) {
				break;
			}
		}
		
		return lights;
	}
	
	private void sendLightData(GLObject entity) {
		if (directionalLight != null) {
			sendDirectionalLightData();
		} else {
			clearDirectionalLight();
		}
		
		int i=0;
		var lights = getClosestLights(entity);
		for (var light : lights) {
			sendPointLightData(light, i);
			i++;
		}
		for (int j=i; j<GLRoomHandler.POINTLIGHTCOUNT; j++) {
			clearPointLight(j);
		}
	}
	
	private void clearDirectionalLight() {
		Vector3f blank = new Vector3f(0.0f);
		
		entityProgram.updateDirLightDirection(blank);
		entityProgram.updateDirLightAmbient(blank);
		entityProgram.updateDirLightDiffuse(blank);
		entityProgram.updateDirLightSpecular(blank);
	}
	
	private void clearPointLight(int count) {
		Vector3f blank = new Vector3f(0.0f);
		
		entityProgram.updatePointLightPosition(blank, count);
		entityProgram.updatePointLightAmbient(blank, count);
		entityProgram.updatePointLightDiffuse(blank, count);
		entityProgram.updatePointLightSpecular(blank, count);
	}
	
	private void sendDirectionalLightData() {
		Vector3f direction = getLightDirection(directionalLight);
		Vector3f ambient = getLightAmbience(directionalLight);
		Vector3f diffuse = getLightDiffuse(directionalLight);
		Vector3f specular = getLightSpecular(directionalLight);
		
		entityProgram.updateDirLightDirection(direction);
		entityProgram.updateDirLightAmbient(ambient);
		entityProgram.updateDirLightDiffuse(diffuse);
		entityProgram.updateDirLightSpecular(specular);
	}
	
	private void sendPointLightData(Light light, int count) {
		Vector3f position = getLightPosition(lightMap.get(light));
		Vector3f ambient = getLightAmbience(light);
		Vector3f diffuse = getLightDiffuse(light);
		Vector3f specular = getLightSpecular(light);
		
		entityProgram.updatePointLightPosition(position, count);
		entityProgram.updatePointLightAmbient(ambient, count);
		entityProgram.updatePointLightDiffuse(diffuse, count);
		entityProgram.updatePointLightSpecular(specular, count);
	}
	
	private Vector3f getLightAmbience(Light light) {
		Vector3f colour = getLightColor(light);
		float intensity = (float) light.getAmbient();
		colour = colour.mul(intensity);
		
		return colour;
	}
	
	private Vector3f getLightDirection(Light light) {
		Vector3f position = getLightPosition(dirLightObject);
		Vector3f origin = new Vector3f(0.0f);
		
		Vector3f direction = origin.sub(position);
		direction = direction.normalize();
		
		return direction;
	}
	
	private Vector3f getLightDiffuse(Light light) {
		Vector3f colour = getLightColor(light);
		float intensity = (float) light.getDiffuse();
		colour = colour.mul(intensity);
		
		return colour;
	}
	
	private Vector3f getLightSpecular(Light light) {
		Vector3f colour = getLightColor(light);
		float intensity = (float) light.getSpecular();
		colour = colour.mul(intensity);
		
		return colour;
	}
	
	private Vector3f getLightColor(Light light) {
		float red = light.getColor().getRed();
		float green = light.getColor().getGreen();
		float blue = light.getColor().getBlue();
		
		red = red / 255.0f;
		green = green / 255.0f;
		blue = blue / 255.0f;
		
		return new Vector3f(red, green, blue);
	}
	
	private Vector3f getLightPosition(GLObject glObject) {
		var vertex = glObject.getVertices().get(0); //bottom left vertex;
		
		Vector4f lightPosition = new Vector4f(vertex.getElements()[0],
				vertex.getElements()[1],
				vertex.getElements()[2],
				vertex.getElements()[3]);
		var model = glObject.getModelMatrix();
		
		lightPosition = lightPosition.mul(model);
		
		return new Vector3f(lightPosition.x, lightPosition.y, lightPosition.z);
	}

	@Override
	protected void globalSetup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void resetGlobal() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resetState() {
		super.resetState();
		lightSources = new ArrayList<Light>();
		lightMap = new HashMap<Light, GLObject>();
		directionalLight = null;
	}

}
