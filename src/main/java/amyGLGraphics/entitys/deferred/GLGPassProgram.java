package amyGLGraphics.entitys.deferred;

import amyGLGraphics.entitys.GLWorldProgram;

public class GLGPassProgram extends GLWorldProgram {

	@Override
	protected void createShaders() {
		createVertexShader("shaders/geompassvertex.glsl");
		createFragmentShader("shaders/geompassfragment.glsl");
	}
	
	@Override
	protected void setUpVars() {
		super.setUpVars();
	}
}
