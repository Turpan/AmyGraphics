package amyGLGraphics.IO;

import java.awt.image.BufferedImage;

public class DecodedPNG {
	byte[] data;
	private int width;
	private int height;
	public DecodedPNG(BufferedImage png) {
		if (png == null) {
			throw new NullPointerException("Sprite cannot be null");
		}
		data = new byte[png.getWidth() * png.getHeight() * 4];
		width = png.getWidth();
		height = png.getHeight();
		decodePNG(png);
	}
	private void decodePNG(BufferedImage png) {
		int counter = 0;
		for (int y=0; y<png.getHeight(); y++) {
			for (int x=0; x<png.getWidth(); x++) {
				int rgb = png.getRGB(x, y);
				int red = (rgb >> 16) & 0xFF;
				int green = (rgb >> 8) & 0xFF;
				int blue = (rgb >> 0) & 0xFF;
				int alpha = (rgb >> 24) & 0xFF;
				counter = setByte(red, counter);
				counter = setByte(green, counter);
				counter = setByte(blue, counter);
				counter = setByte(alpha, counter);
			}
		}
	}
	private int setByte(int value, int counter) {
		data[counter] = (byte) value;
		return counter+1;
	}
	public byte[] getData() {
		return data;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
}
