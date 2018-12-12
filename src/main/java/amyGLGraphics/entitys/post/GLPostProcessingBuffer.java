package amyGLGraphics.entitys.post;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;

import org.lwjgl.opengl.GL30;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.GLTextureDepth;
import amyGLGraphics.base.GLFrameBuffer;
import amyGLGraphics.base.GLWindow;

public class GLPostProcessingBuffer extends GLFrameBuffer {

	public GLPostProcessingBuffer() {
		super(GLWindow.getWindowWidth(), GLWindow.getWindowHeight());
	}

	@Override
	protected void setupFrameBuffer(int width, int height) {
		GLTextureColour colourTexture = new GLTextureColour(width, height, GL30.GL_RGB16F, GL_RGB, GL_FLOAT, GL_LINEAR);
		colourTextures.put(colourTexture, GL30.GL_COLOR_ATTACHMENT0);
		depthTexture = new GLTextureDepth(width, height);
	}

}
