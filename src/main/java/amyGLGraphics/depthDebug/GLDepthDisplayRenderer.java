package amyGLGraphics.depthDebug;

import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;

public class GLDepthDisplayRenderer extends GLRenderer {

	private GLDepthDisplayProgram depthProgram;

	@Override
	protected void createProgram() {
		depthProgram = new GLDepthDisplayProgram();
	}

	@Override
	protected void updateUniversalUniforms() {

	}

	@Override
	protected void updateUniforms(GLObject object) {

	}

	@Override
	protected GLProgram getProgram() {
		return depthProgram;
	}

	@Override
	protected void globalSetup() {

	}

	@Override
	protected void resetGlobal() {

	}

}
