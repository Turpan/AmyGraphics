package amyGLGraphics.Interface;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.GL11;

import amyGLGraphics.base.GLGraphicsHandler;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;

public class GLInterfaceRenderer extends GLRenderer {

	private GLInterfaceProgram program;

	@Override
	protected void createProgram() {
		program = new GLInterfaceProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		program.updateGamma(GLGraphicsHandler.gamma);
	}

	@Override
	protected void updateUniforms(GLObject object) {
		if (!(object instanceof GLComponent)) {
			return;
		}

		GLComponent component = (GLComponent) object;

		program.useTexture(component.hasTexture());
		program.updateAlpha(component.getComponent().getAlpha());
	}

	@Override
	protected GLProgram getProgram() {
		return program;
	}

	@Override
	protected void globalSetup() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
	}

	@Override
	protected void resetGlobal() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}

}
