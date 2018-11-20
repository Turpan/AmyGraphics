package amyGLGraphics.Interface;

import amyGLGraphics.base.GLProgram;

public class GLInterfaceProgram extends GLProgram {

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
