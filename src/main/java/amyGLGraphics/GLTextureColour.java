package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.GL_RGB;

import java.nio.FloatBuffer;

public class GLTextureColour extends GLTexture {

	protected int width;
	protected int height;

	public GLTextureColour(int width, int height) {
		this.width = width;
		this.height = height;
		createTexture();
	}

	public GLTextureColour(int width, int height, int internalformat, int format, int data, int filtering) {
		this.width = width;
		this.height = height;
		createTexture(internalformat, format, data, filtering);
	}

	@Override
	protected void createTexture() {
		createTexture(GL_RGB, GL_RGB, GL_UNSIGNED_BYTE, GL_LINEAR);
	}

	protected void createTexture(int internalformat, int format, int data, int filtering) {
		textureID = glGenTextures();
		FloatBuffer buffer = null;
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexImage2D(GL_TEXTURE_2D, 0, internalformat, width, height,
				0, format, data, buffer);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filtering);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filtering);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public int getTextureType() {
		return GL_TEXTURE_2D;
	}

	@Override
	public boolean isSemiTransparent() {
		return false;
	}

}
