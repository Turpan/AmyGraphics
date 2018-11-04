package amyGLGraphics.blur;

import org.joml.Vector2f;

import amyGLGraphics.base.GLProgram;

public class GLBlurCubeProgram extends GLBlurProgram {

	@Override
	protected void createShaders() {
		createVertexShader("shaders/blurpointvertex.glsl");
		createFragmentShader("shaders/blurpointfragment.glsl");
	}
}
