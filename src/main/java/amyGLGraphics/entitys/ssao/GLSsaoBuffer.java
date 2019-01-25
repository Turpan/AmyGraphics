package amyGLGraphics.entitys.ssao;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.GLTextureDepth;
import amyGLGraphics.base.GLFrameBuffer;
import amyGLGraphics.base.GLWindow;

public class GLSsaoBuffer extends GLFrameBuffer {
	
	public GLSsaoBuffer() {
		super(GLWindow.getWindowWidth(), GLWindow.getWindowHeight());
	}

	@Override
	protected void setupFrameBuffer(int width, int height) {
		GLTextureColour colourTexture = new GLTextureColour(width, height, GL30.GL_R16F, GL11.GL_RED, GL_FLOAT, GL_LINEAR);
		
		colourTextures.put(colourTexture, GL30.GL_COLOR_ATTACHMENT0);
		depthTexture = new GLTextureDepth(width, height);
	}
}
