package amyGLGraphics;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GLTextureCache {
	private static Map<BufferedImage, GLTexture2D> diffuseTextureMap = new HashMap<BufferedImage, GLTexture2D>();
	private static GLTextureCube skyboxTexture;
	
	public static void addTexture(BufferedImage key, GLTexture2D value) {
		diffuseTextureMap.put(key, value);
	}
	
	public static void removeTexture(BufferedImage key) {
		diffuseTextureMap.remove(key);
	}
	
	public static GLTexture2D getTexture(BufferedImage key) {
		return diffuseTextureMap.get(key);
	}
	
	public static boolean hasTexture(BufferedImage key) {
		return diffuseTextureMap.containsKey(key);
	}
	
	public static void setSkybox(GLTextureCube texture) {
		if (skyboxTexture != null) {
			skyboxTexture.unbindTexture();
		}
		skyboxTexture = texture;
	}
	
	public static GLTextureCube getSkyboxTexture() {
		return skyboxTexture;
	}
}
