package amyGLGraphics.entitys;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import amyGLGraphics.GLTexture2D;
import amyGLGraphics.GLTextureCache;
import amyGLGraphics.GLTextureCube;
import amyGLGraphics.IO.GraphicsUtils;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.debug.GLFrameBufferDisplay;
import amyGraphics.Texture;
import movement.Entity;
import movement.Light;
import movement.LightType;
import movement.Room;
import movement.RoomListener;

public class GLRoom implements RoomListener {

	private GLShadowMap shadowBuffer;

	private Light dirLight;
	private List<Light> lights = new ArrayList<Light>();

	private Map<Entity, GLEntity> entityMap = new HashMap<Entity, GLEntity>();
	private GLSkyBox skyBox = new GLSkyBox();
	private GLFrameBufferDisplay background = new GLFrameBufferDisplay();

	private Map<Light, GLPointDepthRenderer> lightDepthMap = new HashMap<Light, GLPointDepthRenderer>();
	
	private List<Entity> toAdd = new ArrayList<Entity>();
	private List<Entity> toRemove = new ArrayList<Entity>();

	Room room;

	public GLRoom(Room room, GLShadowMap shadowBuffer) {
		if (room == null) {
			throw new NullPointerException("Room cannot be null");
		}

		if (shadowBuffer == null) {
			throw new NullPointerException("Shadow Buffer cannot be null");
		}

		this.room = room;
		this.shadowBuffer = shadowBuffer;

		room.addListener(this);

		addEntity(room.getContents());
		for (BufferedImage[] background : room.getBackgrounds()) {
			addBackground(background);
		}
	}
	
	public void resolveQueue() {
		Iterator<Entity> addIter = toAdd.iterator();
		while (addIter.hasNext()) {
			Entity entity = addIter.next();
			addEntity(entity);
			addIter.remove();
		}
		
		Iterator<Entity> removeIter = toRemove.iterator();
		while (removeIter.hasNext()) {
			Entity entity = removeIter.next();
			removeEntity(entity);
			removeIter.remove();
		}
	}

	public Room getRoom() {
		return room;
	}

	public Light getDirLight() {
		return dirLight;
	}

	public List<Light> getLights() {
		return lights;
	}

	public Map<Entity, GLEntity> getEntityMap() {
		return entityMap;
	}

	public Map<Light, GLPointDepthRenderer> getLightDepthMap() {
		return lightDepthMap;
	}

	public GLObject getSkyBox() {
		if (!hasBackground()) {
			return null;
		}
		
		changeBackground();
		
		if (useSkybox()) {
			return skyBox;
		} else {
			return background;
		}
	}

	private void addEntity(Entity entity) {
		if (!entityMap.containsKey(entity)) {
			GLEntity object = createBufferedEntity(entity);

			if (object.hasTexture()) {
				createDiffuseTextures(entity);
			}

			object.setDirShadowMap(shadowBuffer.getColourTexture());
		}
		if (entity instanceof Light) {
			addLight(entity);
		}
	}

	private void addEntity(List<Entity> entityList) {
		for (var entity : entityList) {
			addEntity(entity);
		}
	}

	private void removeEntity(Entity entity) {
		unbindEntityBuffer(entity);
		entityMap.remove(entity);
		for (var texture : entity.getTextures()) {
			if (!textureRemains(entity, texture)) {
				unbindTexture(texture);
			}
		}

		if (entity instanceof Light) {
			Light light = (Light) entity;
			if (light.getType() == LightType.POINT) {
				lights.remove(light);
				lightDepthMap.remove(light);
			} else if (light.getType() == LightType.DIRECTIONAL) {
				dirLight = null;
			}
		}
	}

	private void removeEntity(List<Entity> entityList) {
		for (var entity : entityList) {
			removeEntity(entity);
		}
	}

	private void addLight(Entity entity) {
		var bufferedentity = entityMap.get(entity);
		var light = (Light) entity;
		if (light.getType() == LightType.POINT) {
			GLPointDepthRenderer renderer = new GLPointDepthRenderer(light, bufferedentity);
			lightDepthMap.put(light, renderer);

			lights.add(light);
		} else if (light.getType() == LightType.DIRECTIONAL) {
			dirLight = light;
		}

		bufferedentity.universalColour(GraphicsUtils.colourToVec3(light.getColor()).mul((float) light.getDiffuse()));
	}
	
	public float getBlend() {
		return room.getBackgroundBlend();
	}
	
	private void changeBackground() {
		skyBox.getTextures().clear();
		background.getTextures().clear();
		
		setSkyboxTexture(room.getActiveBackground1(), 0);
		setSkyboxTexture(room.getActiveBackground2(), 1);
	}
	
	private void setSkyboxTexture(BufferedImage[] background, int target) {
		if (background == null) {
			return;
		}
		
		if (background.length == 1) {
			this.background.addTexture(GLTextureCache.getBackground(background), GL13.GL_TEXTURE0 + target);
		} else {
			skyBox.addTexture(GLTextureCache.getSkybox(background), GL13.GL_TEXTURE0 + target);
		}
	}
	
	private void addBackground(BufferedImage[] background) {
		if (background == null) {
			return;
		}
		
		if (background.length == 1) {
			GLTexture2D texture = new GLTexture2D(background[0]);
			GLTextureCache.addBackground(background, texture);
		} else {
			BufferedImage[] cube = new BufferedImage[6];
			int i = 0;
			
			for (BufferedImage face : background) {
				cube[i] = face;
				i++;
				
				if (i == 6) {
					break;
				}
			}
			
			for (int j=i; j<6; j++) {
				cube[j] = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
				cube[j].setRGB(0, 0, Color.BLACK.getRGB());
			}
			
			GLTextureCube texture = new GLTextureCube(cube);
			GLTextureCache.addSkybox(background, texture);
		}
	}
	
	private void removeBackground(BufferedImage[] background) {
		if (background == null) {
			return;
		}
		
		if (background.length == 1) {
			GLTextureCache.removeBackground(background);
		} else {
			GLTextureCache.removeSkybox(background);
		}
	}

	private GLEntity createBufferedEntity(Entity entity) {
		var bufferedEntity = new GLEntity(entity);
		entityMap.put(entity, bufferedEntity);

		return bufferedEntity;
	}

	private void createDiffuseTextures(Entity entity) {
		for (Texture texture : entity.getTextures()) {
			if (!GLTextureCache.hasTexture(texture.getSprite())) {
				GLTexture2D gltexture = new GLTexture2D(texture.getSprite());

				GLTextureCache.addTexture(texture.getSprite(), gltexture);
			}
		}
	}

	private void unbindEntityBuffer(Entity entity) {
		var bufferedentity = entityMap.get(entity);
		bufferedentity.unbindObject();
	}

	private void unbindTexture(Texture texture) {
		GLTextureCache.getTexture(texture.getSprite()).unbindTexture();

		GLTextureCache.removeTexture(texture.getSprite());
	}

	private boolean textureRemains(Entity entity, Texture texture) {
		for (var testedentity : room.getContents()) {
			if (testedentity != entity) {
				for (var testedtexture : testedentity.getTextures()) {
					if (testedtexture.getSprite() == texture.getSprite()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected boolean hasBackground() {
		return room.getActiveBackground1() != null;
	}
	
	protected boolean useSkybox() {
		return room.getActiveBackground1().length == 6;
	}

	public void unbindGL() {
		skyBox.unbindObject();
		background.unbindObject();

		removeEntity(room.getContents());

		for (Light light : lightDepthMap.keySet()) {
			GLPointDepthRenderer renderer = lightDepthMap.get(light);

			renderer.unbindGL();
		}
	}

	@Override
	public void entityAdded(Entity entity) {
		toAdd.add(entity);
	}

	@Override
	public void entityRemoved(Entity entity) {
		toRemove.remove(entity);
	}

	@Override
	public void backgroundAdded(BufferedImage[] background) {
		addBackground(background);
	}

	@Override
	public void backgroundRemoved(BufferedImage[] background) {
		removeBackground(background);
	}

	@Override
	public void backgroundChanged() {
		
	}
}
