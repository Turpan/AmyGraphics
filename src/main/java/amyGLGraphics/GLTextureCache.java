package amyGLGraphics;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javafx.util.Pair;

public class GLTextureCache {
	private static Map<BufferedImage[], GLTexture2D> backgroundTextureMap = new HashMap<BufferedImage[], GLTexture2D>();
	private static Map<BufferedImage[], GLTextureCube> skyboxTextureMap = new HashMap<BufferedImage[], GLTextureCube>();
	
	private static Map<BufferedImage, GLTexture2D> diffuseTextureMap = new HashMap<BufferedImage, GLTexture2D>();

	private static Map<BufferedImage, Pair<GLTexture2D, Integer>> 
		interfaceTextureMap = new HashMap<BufferedImage, Pair<GLTexture2D, Integer>>();
	private static Map<BufferedImage, Pair<GLTexture2D, Integer>>
		fontTextureMap = new HashMap<BufferedImage, Pair<GLTexture2D, Integer>>();;
	
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
		Pair<GLTexture2D, Integer> pair;
		
		if (interfaceTextureMap.containsKey(key)) pair = interfaceTextureMap.get(key);
		else pair = new Pair<GLTexture2D, Integer>(value, 0);
		
		int count = pair.getValue() + 1;
		pair = new Pair<GLTexture2D, Integer>(pair.getKey(), count);
		
		interfaceTextureMap.put(key, pair);
	}

	public static void removeInterfaceTexture(BufferedImage key) {
		Pair<GLTexture2D, Integer> pair = interfaceTextureMap.get(key);
		
		if (pair == null) return;
		
		int count = pair.getValue() - 1;
		if (count <= 0) {
			pair.getKey().unbindTexture();
			interfaceTextureMap.remove(key);
		} else {
			GLTexture2D texture = pair.getKey();
			pair = new Pair<GLTexture2D, Integer>(texture, count);
			interfaceTextureMap.put(key, pair);
		}
	}

	public static GLTexture2D getInterfaceTexture(BufferedImage key) {
		if (!interfaceTextureMap.containsKey(key)) return null;
		
		return interfaceTextureMap.get(key).getKey();
	}

	public static boolean hasInterfaceTexture(BufferedImage key) {
		return interfaceTextureMap.containsKey(key);
	}

	public static GLTexture2D getFontTexture(BufferedImage key) {
		if (!fontTextureMap.containsKey(key)) return null;
		
		return fontTextureMap.get(key).getKey();
	}

	public static boolean hasFontTexture(BufferedImage sprite) {
		return fontTextureMap.containsKey(sprite);
	}

	public static void addFontTexture(BufferedImage key, GLTexture2D value) {
		Pair<GLTexture2D, Integer> pair;
		
		if (fontTextureMap.containsKey(key)) pair = fontTextureMap.get(key);
		else pair = new Pair<GLTexture2D, Integer>(value, 0);
		
		int count = pair.getValue() + 1;
		pair = new Pair<GLTexture2D, Integer>(pair.getKey(), count);
		
		fontTextureMap.put(key, pair);
	}

	public static void removeFontTexture(BufferedImage key) {
		Pair<GLTexture2D, Integer> pair = fontTextureMap.get(key);
		
		if (pair == null) return;
		
		int count = pair.getValue() - 1;
		if (count <= 0) {
			fontTextureMap.remove(key);
		} else {
			GLTexture2D texture = pair.getKey();
			pair = new Pair<GLTexture2D, Integer>(texture, count);
			fontTextureMap.put(key, pair);
		}
	}
}
