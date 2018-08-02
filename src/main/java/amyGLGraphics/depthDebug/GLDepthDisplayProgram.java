package amyGLGraphics.depthDebug;

import amyGLGraphics.GLProgram;

public class GLDepthDisplayProgram extends GLProgram {

	@Override
	protected void createShaders() {
		createVertexShader("shaders/depthdebugvertex.glsl");
		createFragmentShader("shaders/depthdebugfragment.glsl");
	}

}
