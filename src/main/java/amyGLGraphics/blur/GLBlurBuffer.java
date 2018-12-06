package amyGLGraphics.blur;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG32F;

import org.lwjgl.opengl.GL30;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.GLTextureDepth;
import amyGLGraphics.base.GLFrameBuffer;

public class GLBlurBuffer extends GLFrameBuffer {
	
	private GLTextureColour colourTexture;

	public GLBlurBuffer(int width, int height) {
		super(width, height);
	}

	@Override
	protected void setupFrameBuffer(int width, int height) {
		colourTexture = new GLTextureColour(width, height, GL_RG32F, GL_RG, GL_FLOAT, GL_NEAREST);
		colourTextures.put(colourTexture, GL30.GL_COLOR_ATTACHMENT0);
		depthTexture = new GLTextureDepth(width, height);
	}
	
	public GLTextureColour getColourTexture() {
		return colourTexture;
	}
	
	public int getColourTextureID() {
		return colourTexture.getTextureID();
	}

}
