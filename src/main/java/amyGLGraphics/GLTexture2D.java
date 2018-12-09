package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import amyGLGraphics.IO.DecodedPNG;

public class GLTexture2D extends GLTexture {
	private DecodedPNG textureData;
	private boolean transparent;
	private boolean semitransparent;
	static int counter = 0;

	public GLTexture2D(BufferedImage sprite) {
		textureData = new DecodedPNG(sprite);
		createTexture();
		determineTransparency();
	}

	private void determineTransparency() {
		byte[] data = textureData.getData();
		for (int i=0; i<data.length/4; i++) {
			int value = (int) data[(i*4)+3];
			if (value == 0) {
				transparent = true;
			}
			
			if (value < 255) {
				semitransparent = true;
			}
		}
	}

	@Override
	protected void createTexture() {
		int width = textureData.getWidth();
		int height = textureData.getHeight();
		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(textureData.getData().length);
		byteBuffer.put(textureData.getData());
		byteBuffer.flip();
		textureID = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB_ALPHA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
		glGenerateMipmap(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
	}

	public byte[] getTextureData() {
		return textureData.getData();
	}

	@Override
	public boolean isTransparent() {
		return transparent;
	}
	
	@Override
	public boolean isSemiTransparent() {
		return semitransparent;
	}

	@Override
	public int getTextureType() {
		return GL_TEXTURE_2D;
	}

}
