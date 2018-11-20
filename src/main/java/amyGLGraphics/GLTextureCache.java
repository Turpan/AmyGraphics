package amyGLGraphics;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GLTextureCache {
	private static Map<BufferedImage, GLTexture2D> diffuseTextureMap = new HashMap<BufferedImage, GLTexture2D>();
	private static GLTextureCube skyboxTexture;

	private static Map<BufferedImage, GLTexture2D> interfaceTextureMap = new HashMap<BufferedImage, GLTexture2D>();
	private static Map<BufferedImage, GLTexture2D> fontTextureMap = new HashMap<BufferedImage, GLTexture2D>();

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

	public static void addInterfaceTexture(BufferedImage key, GLTexture2D value) {
		interfaceTextureMap.put(key, value);
	}

	public static void removeInterfaceTexture(BufferedImage key) {
		interfaceTextureMap.remove(key);
	}

	public static GLTexture2D getInterfaceTexture(BufferedImage key) {
		return interfaceTextureMap.get(key);
	}

	public static boolean hasInterfaceTexture(BufferedImage key) {
		return interfaceTextureMap.containsKey(key);
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

	public static GLTexture2D getFontTexture(BufferedImage sprite) {
		return fontTextureMap.get(sprite);
	}

	public static boolean hasFontTexture(BufferedImage sprite) {
		return fontTextureMap.containsKey(sprite);
	}

	public static void addFontTexture(BufferedImage key, GLTexture2D value) {
		fontTextureMap.put(key, value);
	}

	public static void removeFontTexture(BufferedImage sprite) {
		fontTextureMap.remove(sprite);
	}
}
