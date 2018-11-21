package amyGLGraphics.Interface;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;

public class GLTextRenderer extends GLRenderer {

	GLTextProgram program;

	@Override
	protected void createProgram() {
		program = new GLTextProgram();
	}

	@Override
	protected void updateUniversalUniforms() {

	}

	@Override
	protected void updateUniforms(GLObject object) {
		if (!(object instanceof GLText)) {
			return;
		}

		GLText text = (GLText) object;

		program.setColour(text.getTextColour());
	}

	@Override
	protected GLProgram getProgram() {
		return program;
	}

	@Override
	protected void globalSetup() {
		glDisable(GL_DEPTH_TEST);
	}

	@Override
	protected void resetGlobal() {
		glEnable(GL_DEPTH_TEST);
	}

}
