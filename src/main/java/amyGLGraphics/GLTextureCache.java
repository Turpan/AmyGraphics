package amyGLGraphics;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GLTextureCache {
	private static Map<BufferedImage[], GLTexture2D> backgroundTextureMap = new HashMap<BufferedImage[], GLTexture2D>();
	private static Map<BufferedImage[], GLTextureCube> skyboxTextureMap = new HashMap<BufferedImage[], GLTextureCube>();
	
	private static Map<BufferedImage, GLTexture2D> diffuseTextureMap = new HashMap<BufferedImage, GLTexture2D>();

	private static Map<BufferedImage, GLTexture2D> interfaceTextureMap = new HashMap<BufferedImage, GLTexture2D>();
	private static Map<BufferedImage, GLTexture2D> fontTextureMap = new HashMap<BufferedImage, GLTexture2D>();
	
	public static void addBackground(BufferedImage[] key, GLTexture2D value) {
		backgroundTextureMap.put(key, value);
	}
	
	public static void removeBackground(BufferedImage[] key) {
		backgroundTextureMap.remove(key);
	}
	
	public static GLTexture2D getBackground(BufferedImage[] key) {
		return backgroundTextureMap.get(key);
	}
	
	public static void addSkybox(BufferedImage[] key, GLTextureCube value) {
		skyboxTextureMap.put(key, value);
	}
	
	public static void removeSkybox(BufferedImage[] key) {
		skyboxTextureMap.remove(key);
	}
	
	public static GLTextureCube getSkybox(BufferedImage[] key) {
		return skyboxTextureMap.get(key);
	}

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
