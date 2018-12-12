package amyGLGraphics.entitys.deferred;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import amyGLGraphics.GLTextureColour;
import amyGLGraphics.GLTextureDepth;
import amyGLGraphics.base.GLFrameBuffer;

public class GLGPassBuffer extends GLFrameBuffer {

	public GLGPassBuffer(int width, int height) {
		super(width, height);
	}

	@Override
	protected void setupFrameBuffer(int width, int height) {
		for (int i=0; i<3; i++) {
			GLTextureColour texture;
			if (i == 2) {
				texture = 
						new GLTextureColour(width, height, GL30.GL_RGBA16F, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, GL11.GL_NEAREST);
			} else {
				texture = 
						new GLTextureColour(width, height, GL30.GL_RGB16F, GL11.GL_RGB, GL11.GL_FLOAT, GL11.GL_NEAREST);
			}
			colourTextures.put(texture, GL30.GL_COLOR_ATTACHMENT0 + i);
		}
		depthTexture = new GLTextureDepth(width, height);
	}

}
