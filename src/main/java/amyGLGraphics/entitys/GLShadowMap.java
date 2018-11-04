package amyGLGraphics.entitys;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG32F;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.GLTextureDepth;
import amyGLGraphics.base.GLFrameBuffer;

public class GLShadowMap extends GLFrameBuffer {

	public GLShadowMap(int width, int height) {
		super(width, height);
	}

	@Override
	protected void setupFrameBuffer(int width, int height) {
		colourTexture = new GLTextureColour(width, height, GL_RG32F, GL_RG, GL_FLOAT, GL_LINEAR);
		depthTexture = new GLTextureDepth(width, height);
	}

}
