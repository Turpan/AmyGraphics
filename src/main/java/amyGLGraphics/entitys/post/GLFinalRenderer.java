package amyGLGraphics.entitys.post;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.GL11;

import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;

public class GLFinalRenderer extends GLRenderer {
	
	private GLFinalProgram program;
	
	private float exposure;
	
	public void setExposure(float exposure) {
		this.exposure = exposure;
	}

	@Override
	protected void createProgram() {
		program = new GLFinalProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		//TODO this will be setting related, later
		program.updateGamma(2.2f);
		program.updateExposure(exposure);
	}

	@Override
	protected void updateUniforms(GLObject object) {
		
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
