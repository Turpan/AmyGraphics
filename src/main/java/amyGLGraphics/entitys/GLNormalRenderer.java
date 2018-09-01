package amyGLGraphics.entitys;

import org.joml.Matrix4f;

import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;

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
