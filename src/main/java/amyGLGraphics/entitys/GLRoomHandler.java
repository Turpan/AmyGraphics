package amyGLGraphics.entitys;

import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

import amyGLGraphics.GLTexture2D;
import amyGLGraphics.GLTextureCache;
import amyGLGraphics.GLTextureCube;
import amyGLGraphics.base.GLCamera;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.depthDebug.GLDepthDisplayRenderer;
import amyGLGraphics.depthDebug.GLFrameBufferDisplay;
import amyGraphics.RoomHandler;
import movement.Entity;
import movement.Light;
import movement.LightType;
import movement.Room;

public class GLRoomHandler extends RoomHandler {
	
	public static final int POINTLIGHTCOUNT = 4;
	public static final int VERTEXPOSITION = 0;
	public static final int NORMALPOSITION = 1;
	public static final int COLOURPOSITION = 2;
	public static final int TEXTUREPOSITION = 3;
	private Map<Entity, GLEntity> entityMap = new HashMap<Entity, GLEntity>();
	private Map<Light, GLPointDepthRenderer> lightDepthMap = new HashMap<Light, GLPointDepthRenderer>();
	private GLSkyBox skyBox = new GLSkyBox();
	private GLNormalRenderer normalRenderer = new GLNormalRenderer();
	private GLEntityRenderer entityRenderer = new GLEntityRenderer();
	private GLLightRenderer lightRenderer = new GLLightRenderer();
	private GLSkyBoxRenderer skyboxRenderer = new GLSkyBoxRenderer();
	private GLDirDepthRenderer dirDepthRenderer = new GLDirDepthRenderer();
	
	public boolean renderNormals;
	public GLRoomHandler() {
		//resetState();
	}
	public GLRoomHandler(Room room) {
		this();
		setRoom(room);
	}
	private void addEntity(Entity entity) {
		if (!entityMap.containsKey(entity)) {
			GLEntity object = createBufferedEntity(entity);
			GLTexture2D texture = createDiffuseTexture(entity);
			
	        object.setDiffuseTexture(texture);
	        object.setDirShadowMap(dirDepthRenderer.getShadowMap().getDepthTexture());
		}
		if (entity instanceof Light) {
			addLight(entity);
		}
	}
	
	private void addLight(Entity entity) {
		var bufferedentity = entityMap.get(entity);
		var light = (Light) entity;
		if (light.getType() == LightType.POINT) {
			GLPointDepthRenderer renderer = new GLPointDepthRenderer(light, bufferedentity);
			lightDepthMap.put(light, renderer);
			
			entityRenderer.addLight(light, bufferedentity, renderer.getShadowMap().getDepthTexture());
		} else if (light.getType() == LightType.DIRECTIONAL) {
			entityRenderer.setDirectionalLight(light, bufferedentity);
			dirDepthRenderer.setDirectionalLight(light, bufferedentity);
		}
		
		bufferedentity.universalColour(colourAsVec(light.getColor()));
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
			GLTextureCache.removeTexture(sprite);
		}
		
		if (entity instanceof Light) {
			Light light = (Light) entity;
			if (light.getType() == LightType.POINT) {
				entityRenderer.removeLight(light);
				lightDepthMap.remove(light);
			} else if (light.getType() == LightType.DIRECTIONAL) {
				entityRenderer.setDirectionalLight(null, null);
			}
		}
	}
	private void removeEntity(List<Entity> entityList) {
		for (var entity : entityList) {
			removeEntity(entity);
		}
	}
	
	private void setBackground(BufferedImage[] background) {
		if (hasBackground()) {
			GLTextureCube skyBoxTexture = new GLTextureCube(background);
			GLTextureCache.setSkybox(skyBoxTexture);
			skyBox.addTexture(skyBoxTexture, GL_TEXTURE0);
		} else {
			skyBox.getTextures().clear();
		}
	}
	/*
	 * Eveything in this class, but this most of all, assumes the openGl context has already been created
	 */
	public void renderRoom() {
		if (hasRoom()) {
			List<GLObject> entitys = new ArrayList<GLObject>();
			
			List<GLObject> opaque = new ArrayList<GLObject>();
			List<GLObject> transparent = new ArrayList<GLObject>();
			
			List<GLObject> lights = new ArrayList<GLObject>();
			for (var entity : room.getContents()) {
				GLObject object = entityMap.get(entity);
				object.update();
				if (isAffectedByLight(entity)) {
					entitys.add(object);
					
					if (object.isTransparent()) {
						transparent.add(object);
					} else {
						opaque.add(object);
					}
				} else {
					lights.add(object);
				}
			}
			
			dirDepthRenderer.render(entitys);
			
			for (Light light : lightDepthMap.keySet()) {
				GLPointDepthRenderer renderer = lightDepthMap.get(light);
				renderer.render(entitys);
			}
			
			entityRenderer.render(opaque);
			
			lightRenderer.render(lights);
			
			
			
			if (renderNormals) {
				normalRenderer.render(entitys);
			}
			
			if (skyBox.isGLBound() && hasBackground()) {
				skyboxRenderer.render(skyBox);
			}
			
			entityRenderer.render(transparent);
			
		}
	}
	
	private GLEntity createBufferedEntity(Entity entity) {
		var bufferedEntity = new GLEntity(entity);
		entityMap.put(entity, bufferedEntity);
		
		return bufferedEntity;
	}
	private GLTexture2D createDiffuseTexture(Entity entity) {
		GLTexture2D texture;
		if (!GLTextureCache.hasTexture(entity.getTexture().getSprite())) {
			texture = new GLTexture2D(entity.getTexture().getSprite());
	        GLTextureCache.addTexture(entity.getTexture().getSprite(), texture);
		} else {
			texture = GLTextureCache.getTexture(entity.getTexture().getSprite());
		}
		
		return texture;
	}
	public void resetState() {
		unBindOpenGL();
		entityRenderer.resetState();
		lightRenderer.resetState();
		skyboxRenderer.resetState();
		normalRenderer.resetState();
		dirDepthRenderer.resetState();
		entityMap.clear();
		lightDepthMap.clear();
		skyBox = new GLSkyBox();
	}
	public void unBindOpenGL() {
		if (hasRoom()) {
			removeEntity(room.getContents());
		}
		if (skyBox.isGLBound()) {
			skyBox.unbindObject();
		}
		for (Light light : lightDepthMap.keySet()) {
			GLPointDepthRenderer renderer = lightDepthMap.get(light);
			renderer.unbindGL();
		}
		entityRenderer.unbindGL();
		lightRenderer.unbindGL();
		skyboxRenderer.unbindGL();
		normalRenderer.unbindGL();
		dirDepthRenderer.unbindGL();
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
		var texture = GLTextureCache.getTexture(entity.getTexture().getSprite());
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
	
	@Override
	public void setRoom(Room room) {
		if (hasRoom()) {
			getRoom().removeListener(this);
			unBindOpenGL();
		}
		super.setRoom(room);
		addEntity(room.getContents());
		setBackground(room.getBackground());
		room.addListener(this);
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
	public void backgroundChanged(BufferedImage[] background) {
		setBackground(background);
	}
	
	private boolean hasBackground() {
		if (!hasRoom()) {
			return false;
		}
		
		if (room.getBackground() == null) {
			return false;
		}
		
		if (room.getBackground().length != 6) {
			return false;
		}
		
		return true;
	}
	
	private Vector3f colourAsVec(Color color) {
		float red = color.getRed();
		float green = color.getGreen();
		float blue = color.getBlue();
		
		red = red / 255.0f;
		green = green / 255.0f;
		blue = blue / 255.0f;
		
		return new Vector3f(red, green, blue);
	}
	
	public void setCamera(GLCamera camera) {
		normalRenderer.setCamera(camera);
		entityRenderer.setCamera(camera);
		lightRenderer.setCamera(camera);
		skyboxRenderer.setCamera(camera);
	}
}
