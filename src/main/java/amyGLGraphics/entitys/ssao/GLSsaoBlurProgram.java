package amyGLGraphics.entitys.ssao;

import amyGLGraphics.base.GLProgram;

public class GLSsaoBlurProgram extends GLProgram{

	@Override
	protected void createShaders() {
		createVertexShader("shaders/ssaoblurvertex.glsl");
		createFragmentShader("shaders/ssaoblurfragment.glsl");
	}

}
