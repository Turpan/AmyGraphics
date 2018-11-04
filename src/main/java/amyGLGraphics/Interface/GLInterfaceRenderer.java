package amyGLGraphics.Interface;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateUniforms(GLObject object) {
		if (!(object instanceof GLComponent)) {
			return;
		}
		
		GLComponent component = (GLComponent) object;
		
		program.useTexture(component.hasTexture());
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
