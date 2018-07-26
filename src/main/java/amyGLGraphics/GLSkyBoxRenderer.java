package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.glDepthFunc;

public class GLSkyBoxRenderer extends GLRenderer {
	
	private GLSkyBoxProgram skyboxProgram;

	@Override
	protected void createProgram() {
		skyboxProgram = new GLSkyBoxProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		skyboxProgram.updateViewMatrix(camera.getCameraMatrix());
	}

	@Override
	protected void updateUniforms(GLObject object) {
		
	}

	@Override
	protected GLProgram getProgram() {
		return skyboxProgram;
	}

	@Override
	protected void globalSetup() {
		glDepthFunc(GL_LEQUAL);
	}

	@Override
	protected void resetGlobal() {
		glDepthFunc(GL_LESS);
	}

}
