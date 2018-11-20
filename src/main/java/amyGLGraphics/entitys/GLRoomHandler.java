package amyGLGraphics.entitys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector4f;
import amyGLGraphics.base.GLCamera;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.blur.GLBlurCubeRenderer;
import amyGLGraphics.blur.GLBlurRenderer;
import amyGLGraphics.depthDebug.GLDepthDisplayRenderer;
import amyGLGraphics.depthDebug.GLFrameBufferDisplay;
import amyGraphics.RoomHandler;
import movement.Entity;
import movement.Light;
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
	@Override
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

	public void setCamera(GLCamera camera) {
		normalRenderer.setCamera(camera);
		entityRenderer.setCamera(camera);
		lightRenderer.setCamera(camera);
		skyboxRenderer.setCamera(camera);
		this.camera = camera;
	}
}
