package amyGraphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontTexture extends Texture {
	
	private Font font;
	
	private Map<Character, Texture> glyphs;
	
	private char character;
	
	private FontTexture(BufferedImage sprite, Font font, Map<Character, Texture> glyphs) {
		super(sprite);
		
		this.font = font;
		this.glyphs = glyphs;
		
		this.character = 'a';
	}
	
	public Font getFont() {
		return font;
	}
	
	@Override
	public Texture getRenderTarget() {
		Texture charTexture = glyphs.get(character);
		
		if (charTexture != null) {
			return charTexture;
		}
		
		return glyphs.get('?');
	}
	
	public Texture getRenderTarget(char toRender) {
		setCharacter(toRender);
		
		return getRenderTarget();
	}
	
	public void setCharacter(char c) {
		this.character = c;
	}
	
	private static BufferedImage createCharImage(char c, Font font, boolean antiAlias) {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = image.createGraphics();
		
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		g.dispose();
		
		int charWidth = fm.charWidth(c);
		int charHeight = fm.getHeight();
		
		image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		g.setFont(font);
		g.setColor(Color.WHITE);
		if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawString(String.valueOf(c), 0, fm.getAscent());
		g.dispose();
		
		return image;
	}
	
	private static Font loadFont(String fileLocation) throws FontFormatException, IOException {
		File file = new File(fileLocation);
		
		Font font = Font.createFont(Font.TRUETYPE_FONT, file);
		
		return font.deriveFont(100f);
	}
	
	public static FontTexture createFontTexture(String fontFile, boolean antiAlias) throws FontFormatException, IOException {
		Font font = loadFont(fontFile);
		
		return createFontTexture(font, antiAlias);
	}
	
	public static FontTexture createFontTexture(Font font, boolean antiAlias) {
		int textureWidth = 0;
		int textureHeight = 0;
		
		List<BufferedImage> charImages = new ArrayList<BufferedImage>();
		List<Character> chars = new ArrayList<Character>();
		
		for (int i=32; i<256; i++) {
			if (i == 127) {
				continue;
			}
			
			char c = (char) i;
			
			chars.add(c);
			BufferedImage charImage = createCharImage(c, font, antiAlias);
			charImages.add(charImage);
			
			textureWidth += charImage.getWidth();
			textureHeight = Math.max(textureHeight, charImage.getHeight());
		}
		
		BufferedImage charChart = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = charChart.createGraphics();
		
		Map<Character, Texture> glyphs = new HashMap<Character, Texture>();
		int x = 0;
		
		for (int i=0; i<charImages.size(); i++) {
			char c = chars.get(i);
			BufferedImage charImage = charImages.get(i);
			
			int charWidth = charImage.getWidth();
			int charHeight = charImage.getHeight();
			
			Texture texture = new Texture(charChart);
			texture.setX(x);
			texture.setY(0);
			texture.setWidth(charWidth);
			texture.setHeight(charHeight);
			
			g.drawImage(charImage, x, 0, null);
			x += charWidth;
			glyphs.put(c, texture);
		}
		
		return new FontTexture(charChart, font, glyphs);
	}

}
