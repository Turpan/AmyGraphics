package amyGLGraphics;

public class GLShadowMap extends GLFrameBuffer {

	public GLShadowMap(int width, int height) {
		super(width, height);
	}

	@Override
	protected void setupFrameBuffer() {
		depthTexture = new GLTextureDepth(width, height);
	}

}
