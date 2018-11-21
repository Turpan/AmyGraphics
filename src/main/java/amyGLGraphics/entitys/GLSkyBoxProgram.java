package amyGLGraphics.entitys;

public class GLSkyBoxProgram extends GLWorldProgram {

	public GLSkyBoxProgram() {

	}

	@Override
	public void createProgram() {
		super.createProgram();
	}

	@Override
	public void setUpVars() {
		super.setUpVars();
	}

	@Override
	protected void createShaders() {
		createVertexShader("shaders/skyvertex.glsl");
		createFragmentShader("shaders/skyfragment.glsl");

		//createFragmentShader("shaders/skyboxdepthfragment.glsl");
	}

}
