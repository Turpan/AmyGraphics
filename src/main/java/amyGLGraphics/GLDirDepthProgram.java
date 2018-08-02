package amyGLGraphics;

import org.joml.Matrix4f;

public class GLDirDepthProgram extends GLWorldProgram{
	
	public static final Matrix4f perspective
	= new Matrix4f().ortho(-10.0f, 10.0f, -10.0f, 10.0f, 1.0f, 15.0f);
	
	private int lightMatrixLocation;

	@Override
	protected void createShaders() {
		createVertexShader("shaders/dirlightdepthvertex.glsl");
		createFragmentShader("shaders/dirlightdepthfragment.glsl");
	}
	
	@Override
	protected void setUpVars() {
		super.setUpVars();
		lightMatrixLocation = queryVariable("lightMatrix");
		updatePersMatrix(perspective);
	}

	public void updateLightMatrix(Matrix4f dirLightMatrix) {
		updateMat4(dirLightMatrix, lightMatrixLocation);
	}

}
