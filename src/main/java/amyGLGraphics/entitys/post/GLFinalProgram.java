package amyGLGraphics.entitys.post;

import amyGLGraphics.base.GLProgram;

public class GLFinalProgram extends GLProgram {
	
	private int gammaLocation;

	@Override
	protected void createShaders() {
		createVertexShader("shaders/buffervertex.glsl");
		createFragmentShader("shaders/bufferfragment.glsl");
	}
	
	private void setUpVars() {
		gammaLocation = queryVariable("gamma");
	}
	
	@Override
	protected void linkProgram() {
		super.linkProgram();
		setUpVars();
	}
	
	public void updateGamma(float gamma) {
		updateFloat(gamma, gammaLocation);
	}

}
