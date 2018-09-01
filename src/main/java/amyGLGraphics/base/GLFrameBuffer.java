package amyGLGraphics.base;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL41;

import amyGLGraphics.GLTexture;
import amyGLGraphics.GLTextureColour;
import amyGLGraphics.GLTextureDepth;

public abstract class GLFrameBuffer {
	protected int bufferID;
	
	protected int width;
	protected int height;
	
	protected GLTextureColour colourTexture;
	protected GLTextureDepth depthTexture;
	
	public GLFrameBuffer(int width, int height) {
		this.width = width;
		this.height = height;
		createFrameBuffer();
	}
	
	protected abstract void setupFrameBuffer();
	
	protected void createFrameBuffer() {
		setupFrameBuffer();
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
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
					depthTexture.getTextureID(), 0);
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		checkFrameBufferErrors();
	}
	
	public void unbindBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, bufferID);
		if (colourTexture != null) {
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
					colourTexture.getTextureType(), 0, 0);
			colourTexture.unbindTexture();
		}
		
		if (depthTexture != null) {
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
					0, 0);
			
			depthTexture.unbindTexture();
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void resetState() {
		unbindBuffer();
		createFrameBuffer();
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
	
	public void checkFrameBufferErrors() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, bufferID);
		int result = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		if (result != GL30.GL_FRAMEBUFFER_COMPLETE) {
			String message = "";
			
			if (result == GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
				message = "Frame buffer incomeplete attachment.";
			} else if (result == GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
				message = "Frame buffer incomeplete draw buffer.";
			} else if (result == GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
				message = "Frame buffer missing attachment.";
			} else if (result == GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE) {
				message = "Frame buffer incomplete multisample.";
			} else if (result == GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
				message = "Frame buffer incomplete read buffer";
			}
			
			System.out.println(message);
		}
	}
	
	public int getColourTextureID() {
		return getTextureID(colourTexture);
	}
	
	public int getDepthTextureID() {
		return getTextureID(depthTexture);
	}
	
	public GLTexture getColourTexture() {
		return colourTexture;
	}
	
	public GLTextureDepth getDepthTexture() {
		return depthTexture;
	}
}
