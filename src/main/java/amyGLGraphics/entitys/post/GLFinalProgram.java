package amyGLGraphics.entitys.post;

import amyGLGraphics.base.GLProgram;

public class GLFinalProgram extends GLProgram {
	
	private int gammaLocation;
	
	private int exposureLocation;

	@Override
	protected void createShaders() {
		createVertexShader("shaders/buffervertex.glsl");
		createFragmentShader("shaders/bufferfragment.glsl");
	}
	
	private void setUpVars() {
		gammaLocation = queryVariable("gamma");
		
		exposureLocation = queryVariable("exposure");
	}
	
	@Override
	protected void linkProgram() {
		super.linkProgram();
		setUpVars();
	}
	
	public void updateGamma(float gamma) {
		updateFloat(gamma, gammaLocation);
	}
	
	public void updateExposure(float exposure) {
		updateFloat(exposure, exposureLocation);
	}

}
