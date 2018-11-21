package amyGLGraphics.blur;

public class GLBlurCubeProgram extends GLBlurProgram {

	@Override
	protected void createShaders() {
		createVertexShader("shaders/blurpointvertex.glsl");
		createFragmentShader("shaders/blurpointfragment.glsl");
	}
}
