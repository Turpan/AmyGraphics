package amyGLGraphics.entitys;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.base.GLCamera;
import amyGLGraphics.base.GLFrameBuffer;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLWindow;
import amyGLGraphics.blur.GLBlurCubeRenderer;
import amyGLGraphics.blur.GLBlurRenderer;
import amyGLGraphics.debug.GLDepthDisplayRenderer;
import amyGLGraphics.debug.GLFrameBufferDisplay;
import amyGLGraphics.debug.GLFustrumRender;
import amyGLGraphics.entitys.deferred.GLGPassRenderer;
import amyGLGraphics.entitys.deferred.GLLPassRenderer;
import amyGLGraphics.entitys.post.GLFinalRenderer;
import amyGLGraphics.entitys.post.GLFxaaRenderer;
import amyGLGraphics.entitys.post.GLPostProcessingBuffer;
import amyGLGraphics.entitys.ssao.GLSsaoRenderer;
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
	private GLBackgroundRenderer backgroundRenderer = new GLBackgroundRenderer();
	
	private GLDirDepthRenderer dirDepthRenderer = new GLDirDepthRenderer();
	private GLBlurRenderer blurRenderer = new GLBlurRenderer();
	private GLBlurCubeRenderer blurCubeRenderer = new GLBlurCubeRenderer();
	private GLDepthDisplayRenderer displayRenderer = new GLDepthDisplayRenderer();
	
	private GLFrameBufferDisplay lPassObject = new GLFrameBufferDisplay();
	private GLGPassRenderer gPassRenderer = new GLGPassRenderer();
	private GLLPassRenderer lPassRenderer = new GLLPassRenderer();
	//private GLSsaoRenderer ssaoRenderer = new GLSsaoRenderer();

	private GLFrameBufferDisplay displayObject = new GLFrameBufferDisplay();
	
	private GLFustrumRender fustrumObject = new GLFustrumRender();
	
	private GLFrameBuffer postProcessingBuffer1 = new GLPostProcessingBuffer();
	private GLFrameBuffer postProcessingBuffer2 = new GLPostProcessingBuffer();
	private GLFinalRenderer finalRenderer = new GLFinalRenderer();
	
	private GLFxaaRenderer fxaaRenderer = new GLFxaaRenderer();

	private GLCamera camera;

	public static boolean shadows = true;
	public static boolean softShadows = false;
	
	//TODO temp
	public static boolean fxaa = false;
	
	public static boolean eyeAdaption = true;
	
	public static boolean ssao = true;

	public boolean renderNormals = false;
	
	private float currentExposure = 1.0f;

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
			
			clearBuffer(postProcessingBuffer1);
			clearBuffer(postProcessingBuffer2);
			
			GLRoom glRoom = roomObjects.get(getActiveRoom());

			List<GLObject> entitys = new ArrayList<GLObject>();

			List<GLObject> opaque = new ArrayList<GLObject>();
			List<GLObject> transparent = new ArrayList<GLObject>();
			List<GLObject> semiTransparent = new ArrayList<GLObject>();

			List<GLObject> lights = new ArrayList<GLObject>();

			Map<Entity, GLEntity> entityMap = glRoom.getEntityMap();
			Map<Light, GLPointDepthRenderer> lightDepthMap = glRoom.getLightDepthMap();

			GLObject skyBox = glRoom.getSkyBox();
			
			setUpLPassObject(lightDepthMap);

			for (var entity : getActiveRoom().getContents()) {
				GLObject object = entityMap.get(entity);
				object.update();
				
				if (!entity.isVisible()) continue;
				
				if (isAffectedByLight(entity)) entitys.add(object);
				
				if (!camera.isInFustrum(object)) continue;
				
				if (isAffectedByLight(entity)) {
					if (object.isSemiTransparent()) {
						semiTransparent.add(object);
					} else if (object.isTransparent()) {
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
			sortTransparent(semiTransparent);

			if (shadows) {
				dirDepthRenderer.setSoftShadow(softShadows);
				dirDepthRenderer.setDirectionalLight(entityMap.get(glRoom.getDirLight()));
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
						blurCubeRenderer.blur(renderer.getShadowMap());
					}
				}
			}
			
			//displayObject.getTextures().clear();
			//displayObject.getTextures().put(dirDepthRenderer.getShadowMap().getColourTexture(), GL13.GL_TEXTURE0);
			//displayObject.getTextures().put(gPassRenderer.getPassBuffer().getColourTextures().get(0), GL13.GL_TEXTURE0);

			entityRenderer.setShadow(shadows);
			entityRenderer.setSoftShadow(softShadows);
			
			gPassRenderer.clearBuffer();
			gPassRenderer.render(opaque);
			gPassRenderer.render(transparent);
			
			if (ssao) {
				//ssaoRenderer.render(displayObject, gPassRenderer.getPassBuffer());
			}
			
			lPassRenderer.setShadow(shadows);
			lPassRenderer.setSoftShadow(softShadows);
			lPassRenderer.render(lPassObject, postProcessingBuffer1);
			
			copyDepthBuffer(gPassRenderer.getPassBuffer());
			entityRenderer.render(semiTransparent, postProcessingBuffer1);

			lightRenderer.render(lights, postProcessingBuffer1);

			if (renderNormals) {
				normalRenderer.render(entitys);
			}

			if (glRoom.hasBackground() && skyBox.isGLBound()) {
				if (glRoom.useSkybox()) {
					skyboxRenderer.setBlend(glRoom.getBlend());
					skyboxRenderer.render(skyBox, postProcessingBuffer1);
				} else {
					backgroundRenderer.setBlend(glRoom.getBlend());
					backgroundRenderer.render(skyBox, postProcessingBuffer1);
				}
			}
			
			boolean source1stBuffer = true;
			
			currentExposure = eyeAdaption ? calculateSceneExposure() : 1.0f;
			
			if (fxaa) {
				GLFrameBuffer source = source1stBuffer ? postProcessingBuffer1 : postProcessingBuffer2;
				GLFrameBuffer dest = source1stBuffer ? postProcessingBuffer2 : postProcessingBuffer1;
				source1stBuffer = !source1stBuffer;
				
				addBufferTexture(source);
				fxaaRenderer.render(displayObject, dest);
			}
			
			//final pass
			GLFrameBuffer source = source1stBuffer ? postProcessingBuffer1 : postProcessingBuffer2;
			
			addBufferTexture(source);
			finalRenderer.setExposure(currentExposure);
			finalRenderer.render(displayObject);
			
			//GL11.glDisable(GL11.GL_CULL_FACE);
			//lightRenderer.render(fustrumObject);
			//normalRenderer.render(fustrumObject);
			//GL11.glEnable(GL11.GL_CULL_FACE);

			//addBufferTexture(ssaoRenderer.getSSAOBuffer());
			//displayRenderer.render(displayObject);
		}
	}
	
	private float calculateSceneExposure() {
		GLTextureColour  hdrRender = postProcessingBuffer1.getColourTextures().get(0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, hdrRender.getTextureID());
		GL30.glGenerateMipmap(GL_TEXTURE_2D);
		int level = (int) (Math.floor(Math.log(Math.max(postProcessingBuffer1.getWidth(),
				postProcessingBuffer1.getHeight())) / Math.log(2)));
		int size = GL11.glGetTexLevelParameteri(GL_TEXTURE_2D, level, GL11.GL_TEXTURE_WIDTH)
				* GL11.glGetTexLevelParameteri(GL_TEXTURE_2D, level, GL11.GL_TEXTURE_HEIGHT) * 3;
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		GL11.glGetTexImage(GL_TEXTURE_2D, level, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
		
		float lumaAverage = 0.0f;
		
		for (int i=0; i<size/3; i++) {
			float red = buffer.get((i * 3));
			float green = buffer.get((i * 3) + 1);
			float blue = buffer.get((i * 3) + 2);
			
			red *= 0.299;
			green *= 0.587;
			blue *= 0.111;
			
			float luma = red + green + blue;
			lumaAverage += luma;
		}
		
		final float jumpSize = 0.01f;
		
		lumaAverage /= (size / 3);
		
		float exposure = 0.5f / lumaAverage;
		
		float jump = jumpSize * Math.signum(exposure - currentExposure);
		
		float futureExposure = currentExposure + jump;
		if (Math.abs(exposure - futureExposure) <= jumpSize && Math.abs(exposure - futureExposure) >= 0.0f) {
			futureExposure = exposure;
		}
		
		return futureExposure;
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
	
	private void copyDepthBuffer(GLFrameBuffer buffer) {
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buffer.getBufferID());
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, postProcessingBuffer1.getBufferID());
		GL30.glBlitFramebuffer(0, 0, buffer.getWidth(), buffer.getHeight(),
				0, 0, postProcessingBuffer1.getWidth(), postProcessingBuffer1.getHeight(), GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	private void clearBuffer(GLFrameBuffer buffer) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer.getBufferID());
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	private void addBufferTexture(GLFrameBuffer buffer) {
		displayObject.getTextures().clear();
		displayObject.getTextures().put(buffer.getColourTextures().get(0), GL13.GL_TEXTURE0);
	}

	public void resetState() {
		unBindOpenGL();
		entityRenderer.resetState();
		lightRenderer.resetState();
		skyboxRenderer.resetState();
		backgroundRenderer.resetState();
		normalRenderer.resetState();
		dirDepthRenderer.resetState();
		blurRenderer.resetState();
		blurCubeRenderer.resetState();
		displayRenderer.resetState();
		roomObjects.clear();
		gPassRenderer.resetState();
		lPassRenderer.resetState();
		//ssaoRenderer.resetState();
		lPassObject = new GLFrameBufferDisplay();
		displayObject = new GLFrameBufferDisplay();
		fustrumObject = new GLFustrumRender();
		postProcessingBuffer1.resetState();
		postProcessingBuffer2.resetState();
		fxaaRenderer.resetState();
		finalRenderer.resetState();
	}
	public void unBindOpenGL() {
		for (Room room : roomObjects.keySet()) {
			GLRoom glRoom = roomObjects.get(room);
			glRoom.unbindGL();
		}
		entityRenderer.unbindGL();
		lightRenderer.unbindGL();
		skyboxRenderer.unbindGL();
		backgroundRenderer.unbindGL();
		normalRenderer.unbindGL();
		dirDepthRenderer.unbindGL();
		blurRenderer.unbindGL();
		blurCubeRenderer.unbindGL();
		displayRenderer.unbindGL();
		gPassRenderer.unbindGL();
		lPassRenderer.unbindGL();
		//ssaoRenderer.unbindGL();
		lPassObject.unbindObject();
		displayObject.unbindObject();
		fustrumObject.unbindObject();
		postProcessingBuffer1.unbindBuffer();
		postProcessingBuffer2.unbindBuffer();
		fxaaRenderer.unbindGL();
		finalRenderer.unbindGL();
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
		//ssaoRenderer.setCamera(camera);
		this.camera = camera;
	}
}
