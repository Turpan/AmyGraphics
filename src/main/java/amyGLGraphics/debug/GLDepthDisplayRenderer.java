package amyGLGraphics.debug;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.GL11;

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
		glDisable(GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	@Override
	protected void resetGlobal() {
		glEnable(GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

}
