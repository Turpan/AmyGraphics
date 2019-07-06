package amyGLGraphics.entitys.deferred;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.IO.GraphicsUtils;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;
import amyGLGraphics.entitys.GLDirDepthRenderer;
import amyGLGraphics.entitys.GLRoomHandler;
import movement.Light;

public class GLLPassRenderer extends GLRenderer {

	private GLLPassProgram program;
	private List<Light> lightSources;
	private Map<Light, GLObject> lightMap;
	private Map<Light, GLTextureColour> lightDepthMap;
	private Light directionalLight;
	private GLObject dirLightObject;

	private boolean shadow;
	private boolean softShadow;

	public GLLPassRenderer() {
		super();

		lightSources = new ArrayList<Light>();
		lightMap = new HashMap<Light, GLObject>();
		lightDepthMap = new HashMap<Light, GLTextureColour>();
		directionalLight = null;
		dirLightObject = null;
	}
	
	public void setShadow(boolean shadow) {
		this.shadow = shadow;
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
		program = new GLLPassProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		if (camera != null) {
			program.updateViewPosition(camera.getPosition());
		}

		program.updateShadow(shadow);
		program.updateSoftShadow(softShadow);
		sendLightData();
	}

	@Override
	protected void updateUniforms(GLObject object) {
		
	}

	@Override
	protected GLProgram getProgram() {
		return program;
	}

	private void sendLightData() {
		if (directionalLight != null) {
			sendDirectionalLightData();
		} else {
			clearDirectionalLight();
		}

		int i=0;
		var lights = getClosestLights();
		for (var light : lights) {
			sendPointLightData(light, i);
			i++;
		}
		for (int j=i; j<GLRoomHandler.DEFPOINTLIGHTCOUNT; j++) {
			clearPointLight(j);
		}
	}
	
	private List<Light> getClosestLights() {
		Collections.sort(lightSources, (Light l1, Light l2) -> {
			GLObject glLight1 = lightMap.get(l1);
			GLObject glLight2 = lightMap.get(l2);

			Vector4f vec1 = glLight1.getVertices().get(0).xyzwVector().mul(glLight1.getModelMatrix());
			Vector4f vec2 = glLight2.getVertices().get(0).xyzwVector().mul(glLight2.getModelMatrix());
			Vector4f camvec = new Vector4f(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z, 1.0f);

			var distance1 = camvec.distance(vec1);
			var distance2 = camvec.distance(vec2);

			int result = 0;

			if (distance1 < distance2) {
				result = 1;
			} else if (distance2 < distance1) {
				result = -1;
			}

			return result;
		});

		List<Light> lights = new ArrayList<Light>();

		int i = 0;
		for (var light : lightSources) {
			lights.add(light);
			i++;
			if (i == GLRoomHandler.DEFPOINTLIGHTCOUNT) {
				break;
			}
		}

		return lights;
	}

	private void clearDirectionalLight() {
		Matrix4f blankM = new Matrix4f();

		Vector3f blank = new Vector3f(0.0f);

		program.updateDirLightDirection(blank);
		program.updateDirLightAmbient(blank);
		program.updateDirLightDiffuse(blank);
		program.updateDirLightSpecular(blank);
		program.updateDirLightMatrix(blankM);
	}

	private void clearPointLight(int count) {
		Vector3f blank = new Vector3f(0.0f);

		if (count < GLRoomHandler.POINTLIGHTCOUNT) {
			int target = GLLPassProgram.pointDepthTextureUnit + count + GL_TEXTURE0;
			glActiveTexture(target);
			glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		}

		program.updatePointLightPosition(blank, count);
		program.updatePointLightAmbient(blank, count);
		program.updatePointLightDiffuse(blank, count);
		program.updatePointLightSpecular(blank, count);
	}

	private void sendDirectionalLightData() {
		Matrix4f lightMatrix = GLDirDepthRenderer.getDirLightMatrix(dirLightObject);

		Vector3f direction = getLightDirection(directionalLight);
		Vector3f ambient = getLightAmbience(directionalLight);
		Vector3f diffuse = getLightDiffuse(directionalLight);
		Vector3f specular = getLightSpecular(directionalLight);

		program.updateDirLightDirection(direction);
		program.updateDirLightAmbient(ambient);
		program.updateDirLightDiffuse(diffuse);
		program.updateDirLightSpecular(specular);
		program.updateDirLightMatrix(lightMatrix);
	}

	private void sendPointLightData(Light light, int count) {
		GLTextureColour texture = lightDepthMap.get(light);
		
		Vector3f position = getLightPosition(lightMap.get(light));
		Vector3f ambient = getLightAmbience(light);
		Vector3f diffuse = getLightDiffuse(light);
		Vector3f specular = getLightSpecular(light);
		
		int target = GLLPassProgram.pointDepthTextureUnit + count + GL_TEXTURE0;
		glActiveTexture(target);
		glBindTexture(texture.getTextureType(), texture.getTextureID());

		program.updatePointLightPosition(position, count);
		program.updatePointLightAmbient(ambient, count);
		program.updatePointLightDiffuse(diffuse, count);
		program.updatePointLightSpecular(specular, count);
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
		GL11.glDisable(GL11.GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
	}

	@Override
	protected void resetGlobal() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
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
