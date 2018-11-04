package amyGLGraphics.entitys;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG32F;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.GLTextureDepth;
import amyGLGraphics.base.GLFrameBuffer;

public class GLShadowMapPoint extends GLFrameBuffer {

	public GLShadowMapPoint(int width, int height) {
		super(width, height);
	}

	@Override
	protected void setupFrameBuffer(int width, int height) {
		colourTexture = new GLTextureColour(width, height, GL_RG32F, GL_RG, GL_FLOAT, GL_NEAREST);
		depthTexture = new GLTextureDepth(width, height);
	}
	
	public void setSoftShadows(boolean soft) {
		if (soft) {
			colourTexture.changeFiltering(GL_LINEAR);
		} else {
			colourTexture.changeFiltering(GL_NEAREST);
		}
	}

}
