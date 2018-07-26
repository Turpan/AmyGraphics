package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.glDepthFunc;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GLSkyBoxRenderer extends GLRenderer {
	
	private GLSkyBoxProgram skyboxProgram;

	@Override
	protected void createProgram() {
		skyboxProgram = new GLSkyBoxProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		Matrix4f cameraMatrix = new Matrix4f(new Matrix3f(camera.getCameraMatrix()));
		
		skyboxProgram.updateViewMatrix(cameraMatrix);
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
