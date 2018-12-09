package amyGLGraphics.entitys.post;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.GL11;

import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;
import amyGLGraphics.base.GLWindow;

public class GLFxaaRenderer extends GLRenderer {
	
	private GLFxaaProgram program;

	@Override
	protected void createProgram() {
		program = new GLFxaaProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		float invwidth = 1.0f / (float) GLWindow.getWindowWidth();
		float invheight = 1.0f / (float) GLWindow.getWindowHeight();
		
		program.updateInvWidth(invwidth);
		program.updateInvHeight(invheight);
	}

	@Override
	protected void updateUniforms(GLObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	protected GLProgram getProgram() {
		return program;
	}

	@Override
	protected void globalSetup() {
		glDisable(GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	@Override
	protected void resetGlobal() {
		glEnable(GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

}
