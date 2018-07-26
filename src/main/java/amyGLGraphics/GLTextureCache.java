package amyGLGraphics;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GLTextureCache {
	private static Map<BufferedImage, GLTexture> textureMap = new HashMap<BufferedImage, GLTexture>();
	private static GLTextureCube skyboxTexture;
	
	public static void addTexture(BufferedImage key, GLTexture value) {
		textureMap.put(key, value);
	}
	
	public static void removeTexture(BufferedImage key) {
		textureMap.remove(key);
	}
	
	public static GLTexture getTexture(BufferedImage key) {
		return textureMap.get(key);
	}
	
	public static boolean hasTexture(BufferedImage key) {
		return textureMap.containsKey(key);
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
