package amyGLInterface;

import amyGLGraphics.GLProgram;

public class GLInterfaceProgram extends GLProgram {
	public GLInterfaceProgram() {
		
	}
	
	public void createProgram() {
		super.createProgram();
	}

	@Override
	protected void createShaders() {
		createVertexShader("shaders/UIvertex.glsl");
		createFragmentShader("shaders/UIfragment.glsl");
	}
}
