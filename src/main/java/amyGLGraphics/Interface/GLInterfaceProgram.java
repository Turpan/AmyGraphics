package amyGLGraphics.Interface;

import amyGLGraphics.base.GLProgram;

public class GLInterfaceProgram extends GLProgram {

	int gammaLocation;
	int alphaLocation;
	int colourRoutineLocation;

	public GLInterfaceProgram() {

	}

	@Override
	public void createProgram() {
		super.createProgram();
		queryVariables();
	}

	protected void queryVariables() {
		colourRoutineLocation = queryVariable("colourSelector");
		gammaLocation = queryVariable("gamma");
		alphaLocation = queryVariable("alpha");
	}
	
	public void updateGamma(float gamma) {
		updateFloat(gamma, gammaLocation);
	}
	
	public void updateAlpha(float alpha) {
		updateFloat(alpha, alphaLocation);
	}

	public void useTexture(boolean useTexture) {
		int useColour = 1;

		if (useTexture) {
			useColour = 0;
		}

		this.updateInt(useColour, colourRoutineLocation);
	}

	@Override
	protected void createShaders() {
		createVertexShader("shaders/interfacevertex.glsl");
		createFragmentShader("shaders/interfacefragment.glsl");
	}
}
