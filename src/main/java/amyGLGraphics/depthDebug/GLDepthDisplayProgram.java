package amyGLGraphics.depthDebug;

import amyGLGraphics.base.GLProgram;

public class GLDepthDisplayProgram extends GLProgram {

	@Override
	protected void createShaders() {
		createVertexShader("shaders/depthdebugvertex.glsl");
		createFragmentShader("shaders/depthdebugfragment.glsl");
	}

}
