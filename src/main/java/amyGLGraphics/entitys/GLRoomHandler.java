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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import amyGLGraphics.GLTexture2D;
import amyGLGraphics.GLTextureCache;
import amyGLGraphics.GLTextureCube;
import amyGLGraphics.base.GLCamera;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.blur.GLBlurCubeRenderer;
import amyGLGraphics.blur.GLBlurRenderer;
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
	
	private Map<Room, GLRoom> roomObjects = new LinkedHashMap<Room, GLRoom>();
	
	private GLNormalRenderer normalRenderer = new GLNormalRenderer();
	private GLEntityRenderer entityRenderer = new GLEntityRenderer();
	private GLLightRenderer lightRenderer = new GLLightRenderer();
	private GLSkyBoxRenderer skyboxRenderer = new GLSkyBoxRenderer();
	private GLDirDepthRenderer dirDepthRenderer = new GLDirDepthRenderer();
	private GLBlurRenderer blurRenderer = new GLBlurRenderer();
	private GLBlurCubeRenderer blurCubeRenderer = new GLBlurCubeRenderer();
	private GLDepthDisplayRenderer displayRenderer = new GLDepthDisplayRenderer();
	
	private GLFrameBufferDisplay displayObject = new GLFrameBufferDisplay();
	
	private GLCamera camera;
	
	private boolean softShadows = false;
	
	public boolean renderNormals = false;
	
	public GLRoomHandler() {
		
	}
	
	/*
	 * Eveything in this class, but this most of all, assumes the openGl context has already been created
	 */
	public void renderRoom() {
		if (hasActiveRoom()) {
			GLRoom glRoom = roomObjects.get(getActiveRoom());
			
			List<GLObject> entitys = new ArrayList<GLObject>();
			
			List<GLObject> opaque = new ArrayList<GLObject>();
			List<GLObject> transparent = new ArrayList<GLObject>();
			
			List<GLObject> lights = new ArrayList<GLObject>();
			
			Map<Entity, GLEntity> entityMap = glRoom.getEntityMap();
			Map<Light, GLPointDepthRenderer> lightDepthMap = glRoom.getLightDepthMap();
			
			GLSkyBox skyBox = glRoom.getSkyBox();
			
			for (var entity : getActiveRoom().getContents()) {
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
			
			sortTransparent(transparent);
			
			dirDepthRenderer.setSoftShadow(softShadows);
			dirDepthRenderer.render(entitys);
			
			for (Light light : lightDepthMap.keySet()) {
				GLPointDepthRenderer renderer = lightDepthMap.get(light);
				renderer.setSoftShadow(softShadows);
				renderer.render(entitys);
			}
			
			if (softShadows) {
				blurRenderer.blur(dirDepthRenderer.getShadowMap());
				for (Light light : lightDepthMap.keySet()) {
					GLPointDepthRenderer renderer = lightDepthMap.get(light);
					//blurCubeRenderer.blur(renderer.getShadowMap());
				}
			}
			
			entityRenderer.setSoftShadow(softShadows);
			entityRenderer.render(opaque);
			
			lightRenderer.render(lights);
			
			if (renderNormals) {
				normalRenderer.render(entitys);
			}
			
			if (skyBox.isGLBound() && glRoom.hasBackground()) {
				skyboxRenderer.render(skyBox);
			}
			
			entityRenderer.render(transparent);
			
			//displayRenderer.render(displayObject);
		}
	}
	
	public void resetState() {
		unBindOpenGL();
		entityRenderer.resetState();
		lightRenderer.resetState();
		skyboxRenderer.resetState();
		normalRenderer.resetState();
		dirDepthRenderer.resetState();
		blurRenderer.resetState();
		blurCubeRenderer.resetState();
		displayRenderer.resetState();
		roomObjects.clear();
		displayObject = new GLFrameBufferDisplay();
	}
	public void unBindOpenGL() {
		for (Room room : roomObjects.keySet()) {
			GLRoom glRoom = roomObjects.get(room);
			glRoom.unbindGL();
		}
		entityRenderer.unbindGL();
		lightRenderer.unbindGL();
		skyboxRenderer.unbindGL();
		normalRenderer.unbindGL();
		dirDepthRenderer.unbindGL();
		blurRenderer.unbindGL();
		blurCubeRenderer.unbindGL();
		displayRenderer.unbindGL();
		displayObject.unbindObject();
	}
	
	private boolean isAffectedByLight(Entity entity) {
		//TODO look at this system later
		return !(entity instanceof Light);
	}
	
	@Override
	public void addRoom(Room room) {
		super.addRoom(room);
		
		GLRoom glRoom = new GLRoom(room, dirDepthRenderer.getShadowMap());
		roomObjects.put(room, glRoom);
	}
	
	@Override
	public void removeRoom(Room room) {
		super.removeRoom(room);
		
		GLRoom glRoom = roomObjects.get(room);
		glRoom.unbindGL();
	}
	
	@Override
	public void setActiveRoom(Room room) {
		super.setActiveRoom(room);
		
		GLRoom glRoom = roomObjects.get(room);
		Map<Entity, GLEntity> entityMap = glRoom.getEntityMap();
		Map<Light, GLPointDepthRenderer> lightDepthMap = glRoom.getLightDepthMap();
		
		entityRenderer.clearLights();
		for (Light light : glRoom.getLights()) {
			entityRenderer.addLight(light, entityMap.get(light), lightDepthMap.get(light).getShadowMap().getColourTexture());
		}
		
		entityRenderer.setDirectionalLight(glRoom.getDirLight(), entityMap.get(glRoom.getDirLight()));
	}
	
	private void sortTransparent(List<GLObject> transparent) {
		/*Collections.sort(transparent, (GLObject ob1, GLObject ob2) -> {
			Vector4f vec1 = ob1.getVertices().get(0).xyzwVector().mul(ob1.getModelMatrix());
			Vector4f vec2 = ob2.getVertices().get(0).xyzwVector().mul(ob2.getModelMatrix());
			Vector3f pos = camera.getPosition();
			Vector4f camvec = new Vector4f(pos.x, pos.y, pos.z, 1);
			
			var distance1 = camvec.distance(vec1);
			var distance2 = camvec.distance(vec2);
			
			return (int) (distance2 - distance1);
		});*/
	}
	
	public void setCamera(GLCamera camera) {
		normalRenderer.setCamera(camera);
		entityRenderer.setCamera(camera);
		lightRenderer.setCamera(camera);
		skyboxRenderer.setCamera(camera);
		this.camera = camera;
	}
}
