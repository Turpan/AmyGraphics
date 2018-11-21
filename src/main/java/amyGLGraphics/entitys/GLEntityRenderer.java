package amyGLGraphics.entitys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.IO.GraphicsUtils;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import movement.Light;

public class GLEntityRenderer extends GLRenderer {

	private GLEntityProgram entityProgram;
	private List<Light> lightSources;
	private Map<Light, GLObject> lightMap;
	private Map<Light, GLTextureColour> lightDepthMap;
	private Light directionalLight;
	private GLObject dirLightObject;

	private boolean softShadow;

	public GLEntityRenderer() {
		super();

		lightSources = new ArrayList<Light>();
		lightMap = new HashMap<Light, GLObject>();
		lightDepthMap = new HashMap<Light, GLTextureColour>();
		directionalLight = null;
		dirLightObject = null;
	}

	public void setSoftShadow(boolean softShadow) {
		this.softShadow = softShadow;
	}

	public void clearLights() {
		lightSources.clear();
		lightMap.clear();
		lightDepthMap.clear();
	}

	public void addLight(Light light, GLObject lightObject, GLTextureColour glTextureColour) {
		lightSources.add(light);
		lightMap.put(light, lightObject);
		lightDepthMap.put(light, glTextureColour);
	}

	public void removeLight(Light light) {
		lightSources.remove(light);
		lightMap.remove(light);
		lightDepthMap.remove(light);
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
		if (camera != null) {
			entityProgram.updateViewMatrix(camera.getCameraMatrix());
			entityProgram.updateViewPosition(camera.getPosition());
		}
		//TODO this will be setting related, later
		entityProgram.updateGamma(2.2f);

		entityProgram.updateSoftShadow(softShadow);
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
		Matrix4f blankM = new Matrix4f();

		Vector3f blank = new Vector3f(0.0f);

		entityProgram.updateDirLightDirection(blank);
		entityProgram.updateDirLightAmbient(blank);
		entityProgram.updateDirLightDiffuse(blank);
		entityProgram.updateDirLightSpecular(blank);
		entityProgram.updateDirLightMatrix(blankM);
	}

	private void clearPointLight(int count) {
		Vector3f blank = new Vector3f(0.0f);

		int target = GLEntityProgram.pointDepthTextureUnit + count + GL_TEXTURE0;
		glActiveTexture(target);
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

		entityProgram.updatePointLightPosition(blank, count);
		entityProgram.updatePointLightAmbient(blank, count);
		entityProgram.updatePointLightDiffuse(blank, count);
		entityProgram.updatePointLightSpecular(blank, count);
	}

	private void sendDirectionalLightData() {
		Matrix4f lightMatrix = GLDirDepthRenderer.getDirLightMatrix(dirLightObject);

		Vector3f direction = getLightDirection(directionalLight);
		Vector3f ambient = getLightAmbience(directionalLight);
		Vector3f diffuse = getLightDiffuse(directionalLight);
		Vector3f specular = getLightSpecular(directionalLight);

		entityProgram.updateDirLightDirection(direction);
		entityProgram.updateDirLightAmbient(ambient);
		entityProgram.updateDirLightDiffuse(diffuse);
		entityProgram.updateDirLightSpecular(specular);
		entityProgram.updateDirLightMatrix(lightMatrix);
	}

	private void sendPointLightData(Light light, int count) {
		GLTextureColour texture = lightDepthMap.get(light);

		Vector3f position = getLightPosition(lightMap.get(light));
		Vector3f ambient = getLightAmbience(light);
		Vector3f diffuse = getLightDiffuse(light);
		Vector3f specular = getLightSpecular(light);

		int target = GLEntityProgram.pointDepthTextureUnit + count + GL_TEXTURE0;
		glActiveTexture(target);
		glBindTexture(texture.getTextureType(), texture.getTextureID());

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
		return GraphicsUtils.colourToVec3(light.getColor());
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
		lightDepthMap = new HashMap<Light, GLTextureColour>();
		directionalLight = null;
		dirLightObject = null;
	}

}
