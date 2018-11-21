package amyGLGraphics.entitys;

import amyGLGraphics.GLTextureDepthCube;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG32F;

import amyGLGraphics.GLTextureColourCube;
import amyGLGraphics.base.GLFrameBuffer;

public class GLShadowMapCube extends GLFrameBuffer {

	public GLShadowMapCube(int width, int height) {
		super(width, height);
	}

	@Override
	protected void setupFrameBuffer(int width, int height) {
		colourTexture = new GLTextureColourCube(width, height, GL_RG32F, GL_RG, GL_FLOAT, GL_NEAREST);
		depthTexture = new GLTextureDepthCube(width, height);
	}

}
