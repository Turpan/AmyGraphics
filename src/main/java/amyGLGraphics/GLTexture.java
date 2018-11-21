package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glTexParameteri;

public abstract class GLTexture {
	protected int textureID;

	public GLTexture() {

	}

	protected abstract void createTexture();

	public void changeFiltering(int filtering) {
		glTexParameteri(getTextureType(), GL_TEXTURE_MIN_FILTER, filtering);
		glTexParameteri(getTextureType(), GL_TEXTURE_MAG_FILTER, filtering);
	}

	public abstract boolean isTransparent();

	public void unbindTexture() {
		glBindTexture(GL_TEXTURE_2D, 0);
		glDeleteTextures(textureID);
	}

	public int getTextureID() {
		return textureID;
	}

	public abstract int getTextureType();

}
