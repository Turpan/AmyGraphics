package amyGLGraphics.entitys;

import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.glDepthFunc;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import amyGLGraphics.base.GLObject;
import amyGLGraphics.base.GLProgram;
import amyGLGraphics.base.GLRenderer;

public class GLBackgroundRenderer extends GLRenderer {

	private GLBackgroundProgram backgroundProgram;
	
	private float blend;

	@Override
	protected void createProgram() {
		backgroundProgram = new GLBackgroundProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		backgroundProgram.updateBlend(blend);
	}

	@Override
	protected void updateUniforms(GLObject object) {

	}
	
	public void setBlend(float blend) {
		this.blend = blend;
	}

	@Override
	protected GLProgram getProgram() {
		return backgroundProgram;
	}

	@Override
	protected void globalSetup() {
		glDepthFunc(GL_LEQUAL);
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	@Override
	protected void resetGlobal() {
		glDepthFunc(GL_LESS);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

}
