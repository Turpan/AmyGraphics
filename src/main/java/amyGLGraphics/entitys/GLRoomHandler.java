package amyGLGraphics.entitys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import amyGLGraphics.base.GLCamera;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.blur.GLBlurCubeRenderer;
import amyGLGraphics.blur.GLBlurRenderer;
import amyGLGraphics.debug.GLDepthDisplayRenderer;
import amyGLGraphics.debug.GLFrameBufferDisplay;
import amyGLGraphics.debug.GLFustrumRender;
import amyGLGraphics.entitys.deferred.GLGPassRenderer;
import amyGLGraphics.entitys.deferred.GLLPassRenderer;
import amyGraphics.RoomHandler;
import movement.Entity;
import movement.Light;
import movement.Room;

public class GLRoomHandler extends RoomHandler {

	public static final int POINTLIGHTCOUNT = 4;
	public static final int DEFPOINTLIGHTCOUNT = 32;
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
	
	private GLFrameBufferDisplay lPassObject = new GLFrameBufferDisplay();
	private GLGPassRenderer gPassRenderer = new GLGPassRenderer();
	private GLLPassRenderer lPassRenderer = new GLLPassRenderer();

	private GLFrameBufferDisplay displayObject = new GLFrameBufferDisplay();
	
	private GLFustrumRender fustrumObject = new GLFustrumRender();

	private GLCamera camera;

	private boolean softShadows = false;

	public boolean renderNormals = false;

	public GLRoomHandler() {

	}

	/*
	 * Eveything in this class, but this most of all, assumes the openGl context has already been created
	 */
	@Override
	public void renderRoom() {
		if (hasActiveRoom()) {
			camera.calculatePlane();
			//fustrumObject.update(camera.getNearPlaneBounds(), camera.getFarPlaneBounds(), camera.getPlaneNormals());
			
			GLRoom glRoom = roomObjects.get(getActiveRoom());

			List<GLObject> entitys = new ArrayList<GLObject>();

			List<GLObject> opaque = new ArrayList<GLObject>();
			List<GLObject> transparent = new ArrayList<GLObject>();

			List<GLObject> lights = new ArrayList<GLObject>();

			Map<Entity, GLEntity> entityMap = glRoom.getEntityMap();
			Map<Light, GLPointDepthRenderer> lightDepthMap = glRoom.getLightDepthMap();

			GLSkyBox skyBox = glRoom.getSkyBox();
			
			setUpLPassObject(lightDepthMap);

			for (var entity : getActiveRoom().getContents()) {
				GLObject object = entityMap.get(entity);
				object.update();
				if (isAffectedByLight(entity)) entitys.add(object);
				
				if (!camera.isInFustrum(object)) {
					continue;
				}
				
				if (isAffectedByLight(entity)) {
					if (object.isTransparent()) {
						transparent.add(object);
					} else {
						opaque.add(object);
					}
				} else {
					lights.add(object);
				}
			}

			sortOpaque(opaque);
			sortTransparent(transparent);

			dirDepthRenderer.setSoftShadow(softShadows);
			dirDepthRenderer.setDirectionalLight(entityMap.get(glRoom.getDirLight()));
			dirDepthRenderer.render(entitys);
			
			displayObject.getTextures().clear();
			//displayObject.getTextures().put(dirDepthRenderer.getShadowMap().getColourTexture(), GL13.GL_TEXTURE0);
			//displayObject.getTextures().put(gPassRenderer.getPassBuffer().getColourTextures().get(0), GL13.GL_TEXTURE0);

			for (Light light : lightDepthMap.keySet()) {
				GLPointDepthRenderer renderer = lightDepthMap.get(light);
				renderer.setSoftShadow(softShadows);
				renderer.render(entitys);
			}

			if (softShadows) {
				blurRenderer.blur(dirDepthRenderer.getShadowMap());
				for (Light light : lightDepthMap.keySet()) {
					GLPointDepthRenderer renderer = lightDepthMap.get(light);
					blurCubeRenderer.blur(renderer.getShadowMap());
				}
			}

			entityRenderer.setSoftShadow(softShadows);
			
			gPassRenderer.render(entitys);
			lPassRenderer.render(lPassObject);
			//entityRenderer.render(opaque);

			//lightRenderer.render(lights);

			if (renderNormals) {
				normalRenderer.render(entitys);
			}

			if (skyBox.isGLBound() && glRoom.hasBackground()) {
				//skyboxRenderer.render(skyBox);
			}

			//entityRenderer.render(transparent);
			
			//GL11.glDisable(GL11.GL_CULL_FACE);
			//lightRenderer.render(fustrumObject);
			//normalRenderer.render(fustrumObject);
			//GL11.glEnable(GL11.GL_CULL_FACE);

			//displayRenderer.render(displayObject);
		}
	}
	
	private void setUpLPassObject(Map<Light, GLPointDepthRenderer> lightDepthMap) {
		lPassObject.getTextures().clear();
		
		lPassObject.addTexture(gPassRenderer.getPassBuffer().getColourTextures().get(0), GL13.GL_TEXTURE0);
		lPassObject.addTexture(gPassRenderer.getPassBuffer().getColourTextures().get(1), GL13.GL_TEXTURE1);
		lPassObject.addTexture(gPassRenderer.getPassBuffer().getColourTextures().get(2), GL13.GL_TEXTURE2);
		
		lPassObject.addTexture(dirDepthRenderer.getShadowMap().getColourTexture(), GL13.GL_TEXTURE3);
		
		/*int i = 0;
		for (Light light : lightDepthMap.keySet()) {
			GLPointDepthRenderer renderer = lightDepthMap.get(light);
			lPassObject.addTexture(renderer.getShadowMap().getColourTexture(), GL13.GL_TEXTURE4 + i);
			i++;
			if (i == POINTLIGHTCOUNT) {
				break;
			}
		}*/
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
		gPassRenderer.resetState();
		lPassRenderer.resetState();
		lPassObject = new GLFrameBufferDisplay();
		displayObject = new GLFrameBufferDisplay();
		fustrumObject = new GLFustrumRender();
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
		gPassRenderer.unbindGL();
		lPassRenderer.unbindGL();
		lPassObject.unbindObject();
		displayObject.unbindObject();
		fustrumObject.unbindObject();
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
		
		lPassRenderer.clearLights();
		for (Light light : glRoom.getLights()) {
			lPassRenderer.addLight(light, entityMap.get(light), lightDepthMap.get(light).getShadowMap().getColourTexture());
		}

		lPassRenderer.setDirectionalLight(glRoom.getDirLight(), entityMap.get(glRoom.getDirLight()));
	}
	
	private void sortTransparent(List<GLObject> transparent) {
		Collections.sort(transparent, (GLObject ob1, GLObject ob2) -> {
			Vector4f vec1 = ob1.getVertices().get(0).xyzwVector().mul(ob1.getModelMatrix());
			Vector4f vec2 = ob2.getVertices().get(0).xyzwVector().mul(ob2.getModelMatrix());
			Vector3f pos = camera.getPosition();
			Vector4f camvec = new Vector4f(pos.x, pos.y, pos.z, 1);

			float distance1 = camvec.distance(vec1);
			float distance2 = camvec.distance(vec2);

			int result = 0;

			if (distance1 < distance2) {
				result = 1;
			} else if (distance2 < distance1) {
				result = -1;
			}

			return result;
		});
	}

	private void sortOpaque(List<GLObject> opaque) {
		Collections.sort(opaque, (GLObject ob1, GLObject ob2) -> {
			Vector4f vec1 = ob1.getVertices().get(0).xyzwVector().mul(ob1.getModelMatrix());
			Vector4f vec2 = ob2.getVertices().get(0).xyzwVector().mul(ob2.getModelMatrix());
			Vector3f pos = camera.getPosition();
			Vector4f camvec = new Vector4f(pos.x, pos.y, pos.z, 1);

			float distance1 = camvec.distance(vec1);
			float distance2 = camvec.distance(vec2);

			int result = 0;

			if (distance1 < distance2) {
				result = -1;
			} else if (distance2 < distance1) {
				result = 1;
			}

			return result;
		});
	}

	public void setCamera(GLCamera camera) {
		normalRenderer.setCamera(camera);
		entityRenderer.setCamera(camera);
		lightRenderer.setCamera(camera);
		skyboxRenderer.setCamera(camera);
		gPassRenderer.setCamera(camera);
		lPassRenderer.setCamera(camera);
		this.camera = camera;
	}
}
