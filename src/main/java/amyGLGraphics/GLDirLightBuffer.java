package amyGLGraphics;

public class GLDirLightBuffer extends GLFrameBuffer {

	public GLDirLightBuffer(int width, int height) {
		super(width, height);
	}

	@Override
	protected void setupFrameBuffer() {
		depthTexture = new GLTextureDepth(width, height);
	}

}
