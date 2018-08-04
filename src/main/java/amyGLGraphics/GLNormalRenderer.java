package amyGLGraphics;

import org.joml.Matrix4f;

public class GLNormalRenderer extends GLRenderer{
	
	private GLWorldProgram normalProgram;

	@Override
	protected void updateUniversalUniforms() {
		if (camera != null) {
			normalProgram.updateViewMatrix(camera.getCameraMatrix());
		}
	}

	@Override
	protected void updateUniforms(GLObject object) {
		normalProgram.updateModelMatrix(object.getModelMatrix());
	}

	@Override
	protected GLProgram getProgram() {
		return normalProgram;
	}

	@Override
	protected void createProgram() {
		normalProgram = new GLNormalProgram();
	}

	@Override
	protected void globalSetup() {
		
	}

	@Override
	protected void resetGlobal() {
		
	}
}
