package amyGLGraphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL41;

public abstract class GLFrameBuffer {
	protected int bufferID;
	
	protected int width;
	protected int height;
	
	protected GLTextureColour colourTexture;
	protected GLTextureDepth depthTexture;
	
	public GLFrameBuffer(int width, int height) {
		this.width = width;
		this.height = height;
		setupFrameBuffer();
		createFrameBuffer();
	}
	
	protected abstract void setupFrameBuffer();
	
	protected void createFrameBuffer() {
		bufferID = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, bufferID);
		if (colourTexture != null) {
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
					GL11.GL_TEXTURE_2D, colourTexture.getTextureID(), 0);
		} else {
			GL11.glDrawBuffer(GL11.GL_NONE);
			GL11.glReadBuffer(GL11.GL_NONE);
		}
		
		if (depthTexture != null) {
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
					GL11.GL_TEXTURE_2D, depthTexture.getTextureID(), 0);
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void unbindBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, bufferID);
		if (colourTexture != null) {
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
					GL11.GL_TEXTURE_2D, 0, 0);
			colourTexture.unbindTexture();
		}
		
		if (depthTexture != null) {
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
					GL11.GL_TEXTURE_2D, 0, 0);
			depthTexture.unbindTexture();
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public int getBufferID() {
		return bufferID;
	}
	
	private int getTextureID(GLTexture texture) {
		if (texture != null) {
			return texture.getTextureID();
		}
		return 0;
	}
	
	public int getColourTextureID() {
		return getTextureID(colourTexture);
	}
	
	public int getDepthTextureID() {
		return getTextureID(depthTexture);
	}
}
