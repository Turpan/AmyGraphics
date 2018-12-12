package amyGLGraphics.entitys;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL13;

import amyGLGraphics.GLTexture2D;
import amyGLGraphics.GLTextureCache;
import amyGLGraphics.GLTextureCube;
import amyGLGraphics.IO.GraphicsUtils;
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

	private Map<Light, GLPointDepthRenderer> lightDepthMap = new HashMap<Light, GLPointDepthRenderer>();

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
		setBackground(room.getBackground());
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

	public GLSkyBox getSkyBox() {
		return skyBox;
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

	private void setBackground(BufferedImage[] background) {
		if (hasBackground()) {
			GLTextureCube skyBoxTexture = new GLTextureCube(background);
			GLTextureCache.setSkybox(skyBoxTexture);
			skyBox.addTexture(skyBoxTexture, GL13.GL_TEXTURE0);
		} else {
			skyBox.getTextures().clear();
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
		if (room.getBackground() == null) {
			return false;
		}

		if (room.getBackground().length != 6) {
			return false;
		}

		return true;
	}

	public void unbindGL() {
		skyBox.unbindObject();

		removeEntity(room.getContents());

		for (Light light : lightDepthMap.keySet()) {
			GLPointDepthRenderer renderer = lightDepthMap.get(light);

			renderer.unbindGL();
		}
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
}
