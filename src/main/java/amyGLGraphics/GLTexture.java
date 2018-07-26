package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

public abstract class GLTexture {
	protected int textureID;
	
	public GLTexture() {
		
	}
	
	protected abstract void createTexture();
	
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
