package amyGLGraphics.entitys;

import amyGLGraphics.GLTextureDepthCube;
import amyGLGraphics.base.GLFrameBuffer;

public class GLShadowMapCube extends GLFrameBuffer {

	public GLShadowMapCube(int width, int height) {
		super(width, height);
	}

	@Override
	protected void setupFrameBuffer() {
		depthTexture = new GLTextureDepthCube(width, height);
	}

}
