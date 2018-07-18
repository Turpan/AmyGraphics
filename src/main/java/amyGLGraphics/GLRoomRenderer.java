package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

import amyGraphics.RoomRenderer;
import movement.Entity;
import movement.Light;
import movement.LightType;
import movement.Room;
import movement.RoomListener;

public class GLRoomRenderer extends RoomRenderer {
	
	public static final int POINTLIGHTCOUNT = 4;
	public static final int VERTEXPOSITION = 0;
	public static final int NORMALPOSITION = 1;
	public static final int COLOURPOSITION = 2;
	public static final int TEXTUREPOSITION = 3;
	private List<Light> lightSources = new ArrayList<Light>();
	private Map<BufferedImage, GLTexture> textureMap = new HashMap<BufferedImage, GLTexture>();
	private Map<Entity, GLEntity> entityMap = new HashMap<Entity, GLEntity>();
	private GLBackground background = new GLBackground();
	private GLEntityProgram entityProgram = new GLEntityProgram();
	private GLWorldProgram lightProgram = new GLWorldProgram();
	private GLNormalRenderer normalRenderer = new GLNormalRenderer();
	private Light directionalLight;
	public boolean renderNormals;
	public GLCamera camera = new GLCamera();
	//TODO camera only public temporarily
	public GLRoomRenderer() {
		resetState();
	}
	public GLRoomRenderer(Room room) {
		this();
		setRoom(room);
	}
	private void addEntity(Entity entity) {
		if (!entityMap.containsKey(entity)) {
			createBufferedEntity(entity);
			createTexture(entity);
		}
		if (entity instanceof Light) {
			addLight(entity);
		}
	}
	
	private void addLight(Entity entity) {
		var light = (Light) entity;
		if (light.getType() == LightType.POINT) {
			lightSources.add(light);
			var bufferedentity = entityMap.get(entity);
			bufferedentity.universalColour(getLightColor(light));
		} else if (light.getType() == LightType.DIRECTIONAL) {
			directionalLight = light;
		}
	}
	
	private void addEntity(List<Entity> entityList) {
		for (var entity : entityList) {
			addEntity(entity);
		}
	}
	private void removeEntity(Entity entity) {
		boolean textureUnbound = unbindEntity(entity);
		entityMap.remove(entity);
		if (textureUnbound) {
			var sprite = entity.getTexture().getSprite();
			textureMap.remove(sprite);
		}
	}
	private void removeEntity(List<Entity> entityList) {
		for (var entity : entityList) {
			removeEntity(entity);
		}
	}
	private void setBackground(BufferedImage sprite) {
		background.setSprite(sprite);
	}
	/*
	 * Eveything in this class, but this most of all, assumes the openGl context has already been created
	 */
	public void renderRoom() {
		updateMatrices();
		if (background.isGLBound()) {
			//renderBackground();
		}
		for (var entity : room.getContents()) {
			if (isAffectedByLight(entity)) {
				renderEntity(entity);
			} else {
				renderStatic(entity);
			}
		}
		
		if (renderNormals) {
			List<GLEntity> entitys = new ArrayList<GLEntity>();
			for (var entity : room.getContents()) {
				if (isAffectedByLight(entity)) {
					entitys.add(entityMap.get(entity));
				}
			}
			normalRenderer.renderNormals(entitys);
		}
		
	}
	
	private void renderEntity(Entity entity) {
		var bufferedtexture = textureMap.get(entity.getTexture().getSprite());
		int textureID = bufferedtexture.getTextureID();
		var bufferedentity = entityMap.get(entity);
		int objectID = bufferedentity.getObjectID();
		int objectIndicesID = bufferedentity.getObjectIndicesBufferID();
		int programID = entityProgram.getProgramID();
		bufferedentity.update();
		entityProgram.updateModelMatrix(bufferedentity.getModelMatrix());
		sendLightData(entity);
		GL20.glUseProgram(programID);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glBindVertexArray(objectID);
		glEnableVertexAttribArray(VERTEXPOSITION);
		glEnableVertexAttribArray(NORMALPOSITION);
		glEnableVertexAttribArray(COLOURPOSITION);
		glEnableVertexAttribArray(TEXTUREPOSITION);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, objectIndicesID);
		glDrawElements(GL_TRIANGLES, bufferedentity.getDrawLength(), GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(VERTEXPOSITION);
		glDisableVertexAttribArray(NORMALPOSITION);
		glDisableVertexAttribArray(COLOURPOSITION);
		glDisableVertexAttribArray(TEXTUREPOSITION);
		glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
	
	private void renderStatic(Entity entity) {
		var bufferedtexture = textureMap.get(entity.getTexture().getSprite());
		int textureID = bufferedtexture.getTextureID();
		var bufferedentity = entityMap.get(entity);
		int objectID = bufferedentity.getObjectID();
		int objectIndicesID = bufferedentity.getObjectIndicesBufferID();
		int programID = lightProgram.getProgramID();
		bufferedentity.update();
		lightProgram.updateModelMatrix(bufferedentity.getModelMatrix());
		GL20.glUseProgram(programID);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glBindVertexArray(objectID);
		glEnableVertexAttribArray(VERTEXPOSITION);
		glEnableVertexAttribArray(COLOURPOSITION);
		glEnableVertexAttribArray(TEXTUREPOSITION);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, objectIndicesID);
		glDrawElements(GL_TRIANGLES, bufferedentity.getDrawLength(), GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(VERTEXPOSITION);
		glDisableVertexAttribArray(COLOURPOSITION);
		glDisableVertexAttribArray(TEXTUREPOSITION);
		glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
	
	private void renderBackground() {
		int textureID = background.getTextureID();
		int objectID = background.getObjectID();
		int objectIndicesID = background.getObjectIndicesBufferID();
		int programID = entityProgram.getProgramID();
		background.update();
		GL20.glUseProgram(programID);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glBindVertexArray(objectID);
		glEnableVertexAttribArray(VERTEXPOSITION);
		glEnableVertexAttribArray(COLOURPOSITION);
		glEnableVertexAttribArray(TEXTUREPOSITION);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, objectIndicesID);
		glDrawElements(GL_TRIANGLES, background.getDrawLength(), GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(VERTEXPOSITION);
		glDisableVertexAttribArray(COLOURPOSITION);
		glDisableVertexAttribArray(TEXTUREPOSITION);
		glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
	private void createBufferedEntity(Entity entity) {
		var bufferedEntity = new GLEntity(entity);
		entityMap.put(entity, bufferedEntity);
	}
	private void createTexture(Entity entity) {
		if (!textureMap.containsKey(entity.getTexture().getSprite())) {
			var texture = new GLTexture(entity.getTexture().getSprite());
	        textureMap.put(entity.getTexture().getSprite(), texture);
		}
	}
	public void resetState() {
		unBindOpenGL();
		lightSources = new ArrayList<Light>();
		textureMap = new HashMap<BufferedImage, GLTexture>();
		entityMap = new HashMap<Entity, GLEntity>();
		entityProgram.createProgram();
		lightProgram.createProgram();
		directionalLight = null;
		normalRenderer.resetState();
	}
	private void unBindOpenGL() {
		if (hasRoom()) {
			removeEntity(room.getContents());
		}
		if (background.isGLBound()) {
			background.unbindObject();
		}
		if (entityProgram.isGLBound()) {
			entityProgram.unbindProgram();
		}
		if (lightProgram.isGLBound()) {
			lightProgram.unbindProgram();
		}
		normalRenderer.unbindOpenGL();
	}
	/*
	 * returns true if texture was unbound
	 */
	private boolean unbindEntity(Entity entity) {
		boolean textureUnbound = false;
		unbindEntityBuffer(entity);
		if (!textureRemains(entity)) {
			unbindTexture(entity);
			textureUnbound = true;
		}
		return textureUnbound;
	}
	private void unbindEntityBuffer(Entity entity) {
		var bufferedentity = entityMap.get(entity);
		bufferedentity.unbindObject();
	}
	private void unbindTexture(Entity entity) {
		var texture = textureMap.get(entity.getTexture().getSprite());
		texture.unbindTexture();
	}
	private boolean textureRemains(Entity entity) {
		for (var testedentity : room.getContents()) {
			if (testedentity != entity) {
				if (entity.getTexture().getSprite().equals(testedentity.getTexture().getSprite())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isAffectedByLight(Entity entity) {
		//TODO look at this system later
		return !(entity instanceof Light);
	}
	
	private List<Light> getClosestLights(Entity entity) {
		Collections.sort(lightSources, (Light l1, Light l2) -> {
			double[] dpos1 = l1.getPosition();
			double[] dpos2 = l2.getPosition();
			double[] enpos = entity.getPosition();
			
			Vector3f vec1 = new Vector3f((float) dpos1[0], (float) dpos1[1], (float) dpos1[2]);
			Vector3f vec2 = new Vector3f((float) dpos2[0], (float) dpos2[1], (float) dpos2[2]);
			Vector3f envec = new Vector3f((float) enpos[0], (float) enpos[1], (float) enpos[2]);
			
			var distance1 = envec.distance(vec1);
			var distance2 = envec.distance(vec2);
			
			return (int) (distance2 - distance1);
		});
		
		List<Light> lights = new ArrayList<Light>();
		
		int i = 0;
		for (var light : lightSources) {
			lights.add(light);
			i++;
			if (i == POINTLIGHTCOUNT) {
				break;
			}
		}
		
		return lights;
	}
	
	private void sendLightData(Entity entity) {
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
		for (int j=i; j<POINTLIGHTCOUNT; j++) {
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
		Vector3f position = getLightPosition(light);
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
		Vector3f position = getLightPosition(light);
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
	
	private Vector3f getLightPosition(Light light) {
		var entity = (Entity) light;
		GLEntity bufferedentity = entityMap.get(entity);
		var vertex = bufferedentity.getVertices().get(0); //bottom left vertex;
		
		Vector4f lightPosition = new Vector4f(vertex.getElements()[0],
				vertex.getElements()[1],
				vertex.getElements()[2],
				vertex.getElements()[3]);
		var model = bufferedentity.getModelMatrix();
		
		lightPosition = lightPosition.mul(model);
		
		return new Vector3f(lightPosition.x, lightPosition.y, lightPosition.z);
	}
	
	private void updateMatrices() {
		entityProgram.updateViewMatrix(camera.getCameraMatrix());
		entityProgram.updateViewPosition(camera.getPosition());
		lightProgram.updateViewMatrix(camera.getCameraMatrix());
		
		if (renderNormals) {
			normalRenderer.updateMatrix(camera.getCameraMatrix());
		}
	}
	
	@Override
	public void setRoom(Room room) {
		if (hasRoom()) {
			getRoom().removeListener(this);
			unBindOpenGL();
		}
		addEntity(room.getContents());
		setBackground(room.getBackground());
		room.addListener(this);
		super.setRoom(room);
	}
	
	@Override
	public void entityAdded(Entity entity) {
		addEntity(entity);
	}
	@Override
	public void entityRemoved(Entity entity) {
		removeEntity(entity);
	}
	@Override
	public void backgroundChanged(BufferedImage background) {
		setBackground(background);
	}
}
